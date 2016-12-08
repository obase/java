package com.github.obase.webc.hiido;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.util.SerializationUtils;

import com.github.obase.kit.StringKit;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodHandler;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.hiido.HiidoKit.Callback;
import com.github.obase.webc.support.security.Principal;
import com.github.obase.webc.support.security.WsidServletMethodProcessor;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class HiidoauthServletMethodProcessor extends WsidServletMethodProcessor implements Callback {

	protected String udbApi;
	protected String agentId;
	protected byte[] agentPwd;
	protected String publicKey;
	protected String homepage;
	protected String hiidoLoginUrl;
	protected JedisPool jedisPool;

	public void setUdbApi(String udbApi) {
		this.udbApi = udbApi;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setAgentPwd(byte[] agentPwd) {
		this.agentPwd = agentPwd;
	}

	public void setAgentPwd(String agentPwd) {
		this.agentPwd = agentPwd.getBytes();
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setHiidoLoginUrl(String hiidoLoginUrl) {
		this.hiidoLoginUrl = hiidoLoginUrl;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public void initMappingRules(FilterConfig filterConfig, Map<String, ServletMethodHandler[]> rules) throws ServletException {

		ServletMethodHandler postHiidoLoginObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				postHiidoLogin(request, response);
			}
		};

		ServletMethodHandler[] arr = new ServletMethodHandler[HttpMethod.values().length];
		Arrays.fill(arr, postHiidoLoginObject);
		rules.put(HiidoKit.LOOKUP_PATH_POST_HIIDO_LOGIN, arr);

		super.initMappingRules(filterConfig, rules);
	}

	@Override
	public final Principal validatePrincipal(HttpServletRequest request, HttpServletResponse response, Wsid wsid) {

		byte[] data = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Transaction tx = jedis.multi();
			Response<byte[]> resp = tx.get(wsid.id);
			tx.expire(wsid.id, (int) (sessionTimeout / 1000));
			tx.exec();

			data = resp.get();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		if (data != null) {
			return (Principal) SerializationUtils.deserialize(data);
		}
		return null;
	}

	@Override
	public void postHiidoLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = Kits.readParam(request, HiidoKit.PARAM_TOKEN);
		if (StringKit.isEmpty(token)) {
			Kits.sendError(response, Webc.ERRNO_INVALID_ACCOUNT, "Invalid account!");
			return;
		}
		Principal principal = validatePrincipal(HiidoKit.getStaffInfoByToken(udbApi, agentId, agentPwd, publicKey, token));
		if (principal == null) {
			Kits.sendError(response, Webc.ERRNO_INVALID_ACCOUNT, "Invalid account!");
			return;
		}

		Wsid wsid = Wsid.valueOf(Webc.GLOBAL_ATTRIBUTE_PREFFIX + principal.getPassport()).resetToken(csrfSecretBytes); // csrf

		byte[] data = SerializationUtils.serialize(principal);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.setex(wsid.id, (int) (sessionTimeout / 1000), data);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		Kits.writeCookie(response, Wsid.COOKIE_NAME, wsid.toHexString(), Wsid.COOKIE_TEMPORY_EXPIRE);
		Kits.sendRedirect(response, Kits.getServletPath(request, StringKit.isNotEmpty(homepage) ? homepage : "/"));
	}

	public void sendBadParameterError(HttpServletResponse resp, int errno, String errmsg) throws IOException {
		if (sendError) {
			Kits.sendError(resp, errno, errmsg);
		} else {
			Kits.writeErrorMessage(resp, errno, errno, errmsg);
		}
	}

	@Override
	protected void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Kits.sendRedirect(response, StringKit.isNotEmpty(hiidoLoginUrl) ? hiidoLoginUrl : HiidoKit.HIIDO_LOGIN_URL);
	}

	@Override
	protected final Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return null;
	}

	// for subclass override
	protected Principal validatePrincipal(Principal principal) {
		return principal;
	}

	// for subclass override
	protected boolean validatePermission(Principal principal, ServletMethod annotation, String lookupPath) {
		return true;
	}

}
