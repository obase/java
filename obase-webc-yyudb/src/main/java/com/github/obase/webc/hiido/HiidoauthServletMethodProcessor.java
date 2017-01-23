package com.github.obase.webc.hiido;

import static com.github.obase.webc.Webc.SC_INVALID_ACCOUNT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import com.github.obase.kit.ObjectKit;
import com.github.obase.kit.StringKit;
import com.github.obase.security.Principal;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodHandler;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.webc.hiido.HiidoKit.Callback;
import com.github.obase.webc.support.security.WsidServletMethodProcessor;
import com.github.obase.webc.yy.UserPrincipal;

public abstract class HiidoauthServletMethodProcessor extends WsidServletMethodProcessor implements Callback {

	protected abstract String getUdbApi();

	protected abstract String getAgentId();

	protected abstract byte[] getAgentPwd();

	protected abstract String getPublicKey();

	protected abstract String getHomepage();

	protected abstract String getHiidoLoginUrl();

	protected abstract JedisPool getJedisPool();

	@Override
	public void setup(FilterInitParam params, Map<String, ServletMethodObject> rules) throws ServletException {

		ServletMethodHandler postHiidoLoginObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				String token = Kits.readParam(request, HiidoKit.PARAM_TOKEN);
				if (!postHiidoLogin(request, response, token)) {
					sendError(response, SC_INVALID_ACCOUNT, SC_INVALID_ACCOUNT, "Invalid account!");
					return;
				}
				Kits.sendRedirect(response, Kits.getServletPath(request, ObjectKit.<String> ifnull(getHomepage(), "/")));
			}
		};

		ServletMethodObject object = new ServletMethodObject(null, HiidoKit.LOOKUP_PATH_POST_HIIDO_LOGIN);
		Arrays.fill(object.handlers, postHiidoLoginObject);
		rules.put(HiidoKit.LOOKUP_PATH_POST_HIIDO_LOGIN, object);

		super.setup(params, rules);
	}

	@Override
	public Principal validateAndExtendPrincipal(Wsid wsid) {

		String data = null;
		Jedis jedis = null;
		try {
			jedis = getJedisPool().getResource();
			Transaction tx = jedis.multi();
			Response<String> resp = tx.get(wsid.id);
			tx.pexpire(wsid.id, timeoutMillis);
			tx.exec();

			data = resp.get();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		if (data != null) {
			return new UserPrincipal().decode(data);
		}
		return null;
	}

	@Override
	public boolean postHiidoLogin(HttpServletRequest request, HttpServletResponse response, String token) throws ServletException, IOException {

		if (StringKit.isEmpty(token)) {
			return false;
		}
		UserPrincipal principal = validatePrincipal(HiidoKit.getStaffInfoByToken(ObjectKit.ifnull(getUdbApi(), HiidoKit.HIIDO_UDB_API), getAgentId(), getAgentPwd(), getPublicKey(), token));
		if (principal == null) {
			return false;
		}

		Wsid wsid = Wsid.valueOf(principal.getPassport()).resetToken(wsidTokenBase); // csrf

		String data = principal.encode();
		Jedis jedis = null;
		try {
			jedis = getJedisPool().getResource();
			jedis.psetex(wsid.id, timeoutMillis, data);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		Kits.writeCookie(response, Wsid.COOKIE_NAME, Wsid.encode(wsid), Wsid.COOKIE_TEMPORY_EXPIRE);

		return true;
	}

	@Override
	protected void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Kits.sendRedirect(response, ObjectKit.<String> ifnull(getHiidoLoginUrl(), HiidoKit.HIIDO_LOGIN_URL));
	}

	@Override
	protected Wsid tryOssLogin(HttpServletRequest request) throws ServletException, IOException {
		return null;
	}

	// for subclass override
	protected UserPrincipal validatePrincipal(UserPrincipal staffInfoByToken) {
		return staffInfoByToken;
	}

	// for subclass override
	@Override
	protected boolean validatePermission(Principal principal, HttpMethod method, String lookupPath) {
		return true;
	}

}
