package com.github.obase.webc.udb;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import com.github.obase.kit.SerialKit;
import com.github.obase.kit.StringKit;
import com.github.obase.security.Principal;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodHandler;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.webc.support.security.WsidServletMethodProcessor;
import com.github.obase.webc.udb.UdbKit.Callback;
import com.github.obase.webc.yy.UserPrincipal;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public abstract class UdbauthServletMethodProcessor extends WsidServletMethodProcessor implements Callback {

	protected abstract String getAppid();

	protected abstract String getAppkey();

	protected abstract String getHomepage();

	protected abstract String getLogoutpage();

	protected abstract JedisPool getJedisPool();

	@Override
	public void setup(FilterInitParam params, Map<String, ServletMethodObject> rules) throws ServletException {

		ServletMethodHandler loginObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.login(request, response, Kits.getNamespace(request));
			}
		};

		ServletMethodHandler genUrlTokenObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.genUrlToken(request, response, Kits.getNamespace(request), getAppid(), getAppkey());
			}
		};

		ServletMethodHandler callbackObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.callback(request, response, Kits.getNamespace(request), getAppid(), getAppkey(), getHomepage(), UdbauthServletMethodProcessor.this);
			}
		};

		ServletMethodHandler denyCallbackObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.denyCallback(request, response);
			}
		};

		ServletMethodHandler logoutObject = new ServletMethodHandler() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.logout(request, response, Kits.getNamespace(request), getAppid(), getAppkey(), getLogoutpage(), UdbauthServletMethodProcessor.this);
			}
		};

		rules.put(UdbKit.LOOKUP_PATH_LOGIN, newServletMethodObject(UdbKit.LOOKUP_PATH_LOGIN, loginObject));
		rules.put(UdbKit.LOOKUP_PATH_GEN_URL_TOKEN, newServletMethodObject(UdbKit.LOOKUP_PATH_GEN_URL_TOKEN, genUrlTokenObject));
		rules.put(UdbKit.LOOKUP_PATH_CALLBACK, newServletMethodObject(UdbKit.LOOKUP_PATH_CALLBACK, callbackObject));
		rules.put(UdbKit.LOOKUP_PATH_DENY_CALLBACK, newServletMethodObject(UdbKit.LOOKUP_PATH_DENY_CALLBACK, denyCallbackObject));
		rules.put(UdbKit.LOOKUP_PATH_LOGOUT, newServletMethodObject(UdbKit.LOOKUP_PATH_LOGOUT, logoutObject));

		super.setup(params, rules);
	}

	private ServletMethodObject newServletMethodObject(String lookupPath, ServletMethodHandler handler) {
		ServletMethodObject object = new ServletMethodObject(null, lookupPath);
		Arrays.fill(object.handlers, handler);
		return object;
	}

	@Override
	public Principal validateAndExtendPrincipal(Wsid wsid) {

		byte[] data = null;
		Jedis jedis = null;
		try {
			jedis = getJedisPool().getResource();
			Transaction tx = jedis.multi();
			Response<byte[]> resp = tx.get(wsid.id);
			tx.pexpire(wsid.id, timeoutMillis);
			tx.exec();

			data = resp.get();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		if (data != null) {
			return (Principal) SerialKit.deserialize(data);
		}
		return null;
	}

	@Override
	public boolean postUdbLogin(HttpServletRequest request, HttpServletResponse response, String yyuid, String[] uProfile) throws IOException {

		UserPrincipal principal = validatePrincipal(yyuid, uProfile);
		if (principal == null) {
			return false;
		}

		Wsid wsid = Wsid.valueOf(principal.getPassport()).resetToken(wsidTokenBase); // csrf

		byte[] data = SerialKit.serialize(principal);
		Jedis jedis = null;
		try {
			jedis = getJedisPool().getResource();
			jedis.psetex(wsid.id, timeoutMillis, data);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		Kits.writeCookie(response, Wsid.COOKIE_NAME, wsid.toHexs(), Wsid.COOKIE_TEMPORY_EXPIRE);

		return true;
	}

	@Override
	public void preUdbLogout(HttpServletRequest request, HttpServletResponse response) {

		Wsid wsid = Kits.getWsid(request);
		if (wsid == null) {
			wsid = Wsid.fromHexs(Kits.readCookie(request, Wsid.COOKIE_NAME));
		}
		Jedis jedis = null;
		try {
			jedis = getJedisPool().getResource();
			jedis.del(wsid.id);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0);
	}

	@Override
	protected void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

	// for subclass override
	protected UserPrincipal validatePrincipal(String yyuid, String[] uProfile) {
		UserPrincipal principal = new UserPrincipal();
		principal.setYyuid(yyuid);
		principal.setPassport(uProfile[0]);
		principal.setRealname(uProfile[0]);
		return principal;
	}

	// for subclass override
	@Override
	protected boolean validatePermission(Principal principal, HttpMethod method, String lookupPath) {
		return true;
	}

}
