package com.github.obase.webc.udb;

import java.io.IOException;
import java.net.URLEncoder;
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
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.support.security.Principal;
import com.github.obase.webc.support.security.SecurityServletMethodProcessor;
import com.github.obase.webc.support.security.SimplePrincipal;
import com.github.obase.webc.udb.UdbKit.Callback;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class UdbauthServletMethodProcessor extends SecurityServletMethodProcessor implements Callback {

	protected String appid;
	protected String appkey;
	protected String homepage;
	protected String logoutpage;
	protected JedisPool jedisPool;

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setLogoutpage(String logoutpage) {
		this.logoutpage = logoutpage;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public void initMappingRules(FilterConfig filterConfig, Map<String, ServletMethodObject[]> rules) throws ServletException {

		ServletMethodObject loginObject = new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.login(request, response, Kits.getNamespace(request));
			}
		};

		ServletMethodObject genUrlTokenObject = new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.genUrlToken(request, response, Kits.getNamespace(request), appid, appkey);
			}
		};

		ServletMethodObject callbackObject = new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.callback(request, response, Kits.getNamespace(request), appid, appkey, homepage, UdbauthServletMethodProcessor.this);
			}
		};

		ServletMethodObject denyCallbackObject = new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.denyCallback(request, response);
			}
		};

		ServletMethodObject logoutObject = new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.logout(request, response, Kits.getNamespace(request), appid, appkey, logoutpage, UdbauthServletMethodProcessor.this);
			}
		};

		rules.put(UdbKit.LOOKUP_PATH_LOGIN, fill(loginObject));
		rules.put(UdbKit.LOOKUP_PATH_GEN_URL_TOKEN, fill(genUrlTokenObject));
		rules.put(UdbKit.LOOKUP_PATH_CALLBACK, fill(callbackObject));
		rules.put(UdbKit.LOOKUP_PATH_DENY_CALLBACK, fill(denyCallbackObject));
		rules.put(UdbKit.LOOKUP_PATH_LOGOUT, fill(logoutObject));

		super.initMappingRules(filterConfig, rules);
	}

	private ServletMethodObject[] fill(ServletMethodObject object) {
		ServletMethodObject[] arr = new ServletMethodObject[HttpMethod.values().length];
		Arrays.fill(arr, object);
		return arr;
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
	public boolean postUdbLogin(HttpServletRequest request, HttpServletResponse response, String yyuid, String[] uProfile) throws IOException {

		Principal principal = validatePrincipal(yyuid, uProfile);
		if (principal == null) {
			sendBadParameterError(response, Webc.ERRNO_INVALID_ACCOUNT, "Invalid account!");
			return false;
		}

		Wsid wsid = Wsid.valueOf(Webc.GLOBAL_ATTRIBUTE_PREFFIX + yyuid).resetToken(csrfSecretBytes); // csrf

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

		return true;
	}

	@Override
	public void preUdbLogout(HttpServletRequest request, HttpServletResponse response) {

		Wsid wsid = Kits.getWsid(request);
		if (wsid == null) {
			wsid = Wsid.decode(Kits.readCookie(request, Wsid.COOKIE_NAME));
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(wsid.id);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0);
	}

	@Override
	public void sendBadParameterError(HttpServletResponse resp, int errno, String errmsg) throws IOException {
		if (sendError) {
			Kits.sendError(resp, errno, errmsg);
		} else {
			Kits.writeErrorMessage(resp, errno, errno, errmsg);
		}
	}

	@Override
	protected void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder psb = new StringBuilder(256);
		psb.append(request.getContextPath()).append(request.getServletPath());
		String queryString = request.getQueryString();
		if (StringKit.isNotEmpty(queryString)) {
			psb.append('?').append(queryString);
		}

		StringBuilder sb = new StringBuilder(256);
		sb.append(Kits.getServletPath(request, UdbKit.LOOKUP_PATH_LOGIN));
		sb.append("?").append(UdbKit.PARAM_URL).append("=").append(URLEncoder.encode(psb.toString(), Webc.CHARSET_NAME));

		Kits.sendRedirect(response, sb.toString());
	}

	@Override
	protected final Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return tryOssLogin(request, response, appid, appkey);
	}

	// for subclass override
	protected Principal validatePrincipal(String yyuid, String[] uProfile) {
		SimplePrincipal principal = new SimplePrincipal();
		principal.setYyuid(yyuid);
		principal.setPassport(uProfile[0]);
		principal.setRealname(uProfile[0]);
		return principal;
	}

	// for subclass override
	protected boolean validatePermission(Principal principal, ServletMethod annotation, String lookupPath) {
		return true;
	}

	// for subclass override
	@Override
	public Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response, String appid, String appkey) throws IOException {
		return null;
	}

}
