package com.github.obase.webc.udb;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.obase.kit.StringKit;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.support.security.Principal;

public class GenericUdbauthFilter implements Filter {

	protected final Log logger = LogFactory.getLog(getClass());

	public static final String INIT_PARAM_SERVLET_PATH_PREFIX = "servletPathPrefix";
	public static final String INIT_PARAM_COOKIE_NAME = "cookieName";
	public static final String INIT_PARAM_TRY_OSS_LOGIN = "tryOssLogin";
	public static final String INIT_PARAM_APPID = "appid";
	public static final String INIT_PARAM_APPKEY = "appkey";
	public static final String INIT_PARAM_HOMEPAGE = "homepage";
	public static final String INIT_PARAM_LOGOUTPAGE = "logoutpage";

	String servletPathPrefix;
	String cookieName;
	boolean tryOssLogin;
	String appid;
	String appkey;
	String homepage;
	String logoutpage;

	UdbKit.Callback callback;
	final Map<String, ServletMethodObject> actions = new HashMap<>();

	private String getStringParameter(FilterConfig filterConfig, String name, String def) {
		String tmp = filterConfig.getInitParameter(name);
		return (tmp == null || tmp.length() == 0) ? def : tmp;
	}

	private boolean getBoolParameter(FilterConfig filterConfig, String name, boolean def) {
		String tmp = filterConfig.getInitParameter(name);
		return (tmp == null || tmp.length() == 0) ? def : Boolean.parseBoolean(tmp);
	}

	private String getRealPath(String servletPathPrefix, String path) {

		if (StringKit.isEmpty(servletPathPrefix)) {
			return path;
		}

		StringBuilder sb = new StringBuilder(128);
		if (servletPathPrefix.charAt(0) != '/') {
			sb.append('/');
		}
		sb.append(servletPathPrefix);
		sb.append(path);
		return sb.toString();
	}

	private <T> T getOptionBean(FilterConfig filterConfig, Class<T> type) {
		ApplicationContext appctx = WebApplicationContextUtils
				.getWebApplicationContext(filterConfig.getServletContext());
		if (appctx != null) {
			try {
				return appctx.getBean(type);
			} catch (BeansException be) {
				logger.error("Faild to get UdbKit.Callback bean from application context!", be);
			}
		}
		return null;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		servletPathPrefix = getStringParameter(filterConfig, INIT_PARAM_SERVLET_PATH_PREFIX, null);
		cookieName = getStringParameter(filterConfig, INIT_PARAM_COOKIE_NAME, Wsid.COOKIE_NAME);
		tryOssLogin = getBoolParameter(filterConfig, INIT_PARAM_TRY_OSS_LOGIN, true);
		appid = getStringParameter(filterConfig, INIT_PARAM_APPID, "");
		appkey = getStringParameter(filterConfig, INIT_PARAM_APPKEY, "");
		homepage = getStringParameter(filterConfig, INIT_PARAM_HOMEPAGE, null);
		logoutpage = getStringParameter(filterConfig, INIT_PARAM_LOGOUTPAGE, UdbKit.LOOKUP_PATH_LOGIN);

		callback = getOptionBean(filterConfig, UdbKit.Callback.class);
		if (callback == null) {
			callback = new GenericUdbauthCallback();
			logger.info("Use default UdbKit.Callback to " + this.getClass().getSimpleName() + "$"
					+ filterConfig.getFilterName());
		}

		actions.put(getRealPath(servletPathPrefix, UdbKit.LOOKUP_PATH_LOGIN), new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.login(request, response, servletPathPrefix);
				;
			}
		});

		actions.put(getRealPath(servletPathPrefix, UdbKit.LOOKUP_PATH_LOGOUT), new ServletMethodObject() {

			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.logout(request, response, servletPathPrefix, appid, appkey, logoutpage, callback);
			}
		});

		actions.put(getRealPath(servletPathPrefix, UdbKit.LOOKUP_PATH_GEN_URL_TOKEN), new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.genUrlToken(request, response, servletPathPrefix, appid, appkey);
			}
		});

		actions.put(getRealPath(servletPathPrefix, UdbKit.LOOKUP_PATH_CALLBACK), new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.callback(request, response, servletPathPrefix, appid, appkey, homepage, callback);
			}
		});

		actions.put(getRealPath(servletPathPrefix, UdbKit.LOOKUP_PATH_DENY_CALLBACK), new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				UdbKit.denyCallback(request, response);
			}
		});

		// store cookieName
		filterConfig.getServletContext().setAttribute(Wsid.COOKIE_NAME, cookieName);

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		ServletMethodObject action = actions.get(request.getServletPath());
		if (action != null) {
			try {
				action.service(request, response);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			return;
		}

		// step1: read session id, if null forward to login page
		Wsid wsid = Wsid.decode(Kits.readCookie(request, cookieName));
		if (wsid == null) {
			if (tryOssLogin) {
				wsid = callback.tryOssLogin(request, response, appid, appkey);
				if (logger.isDebugEnabled()) {
					logger.debug("Try oss login with cookie:" + Kits.readCookieMap(request));
				}
			}
			if (wsid == null) {
				redirectToLoginPage(request, response, servletPathPrefix);
				return;
			}
		} else if (!wsid.validate(Webc.DEFAULT_CSRF_SECRET_BYTES, Long.MAX_VALUE)) {
			Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0); // remove
			Kits.sendError(response, Webc.ERRNO_MISSING_VERIFIER, "Missing verifier!");
			return;
		}
		Kits.writeCookie(response, cookieName, wsid.resetToken(Webc.DEFAULT_CSRF_SECRET_BYTES).toHexString(),
				Wsid.COOKIE_TEMPORY_EXPIRE);
		request.setAttribute(Webc.ATTR_WSID, wsid);

		// step2: get session, if null forward to login page
		Principal principal = callback.validatePrincipal(request, response, wsid);
		if (principal == null) {
			redirectToLoginPage(request, response, servletPathPrefix);
			return;
		}
		request.setAttribute(Webc.ATTR_PRINCIPAL, principal);

		// step3: go on next
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// nothing
	}

	protected void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response,
			String servletPathPrefix) throws ServletException, IOException {
		StringBuilder psb = new StringBuilder(256);
		psb.append(request.getContextPath()).append(request.getServletPath());
		String queryString = request.getQueryString();
		if (StringKit.isNotEmpty(queryString)) {
			psb.append('?').append(queryString);
		}

		StringBuilder sb = new StringBuilder(256);
		sb.append(getRealPath(servletPathPrefix, UdbKit.LOOKUP_PATH_LOGIN));
		sb.append("?").append(UdbKit.PARAM_URL).append("=")
				.append(URLEncoder.encode(psb.toString(), Webc.CHARSET_NAME));

		Kits.sendRedirect(response, sb.toString());
	}
}
