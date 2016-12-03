package com.github.obase.webc.hiido;

import java.io.IOException;
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
import com.github.obase.webc.hiido.HiidoKit.Callback;
import com.github.obase.webc.support.security.Principal;



public class GenericHiidoauthFilter implements Filter {

	protected final Log logger = LogFactory.getLog(getClass());

	public static final String INIT_PARAM_SERVLET_PATH_PREFIX = "servletPathPrefix";
	public static final String INIT_PARAM_COOKIE_NAME = "cookieName";
	public static final String INIT_PARAM_UDB_API = "udbApi";
	public static final String INIT_PARAM_AGENT_ID = "agentId";
	public static final String INIT_PARAM_AGENT_PWD = "agentPwd";
	public static final String INIT_PARAM_PUBLIC_KEY = "publicKey";
	public static final String INIT_PARAM_HOMEPAGE = "homepage";
	public static final String INIT_PARAM_HIIDO_LOGIN_URL = "hiidoLoginUrl";

	String servletPathPrefix;
	String cookieName;
	String udbApi;
	String agentId;
	byte[] agentPwd;
	String publicKey;
	String homepage;
	String hiidoLoginUrl;

	Callback callback;
	final Map<String, ServletMethodObject> actions = new HashMap<>();

	private String getStringParameter(FilterConfig filterConfig, String name, String def) {
		String tmp = filterConfig.getInitParameter(name);
		return (tmp == null || tmp.length() == 0) ? def : tmp;
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
		ApplicationContext appctx = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
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
		udbApi = getStringParameter(filterConfig, INIT_PARAM_UDB_API, "");
		agentId = getStringParameter(filterConfig, INIT_PARAM_AGENT_ID, "");
		agentPwd = getStringParameter(filterConfig, INIT_PARAM_AGENT_PWD, "").getBytes();
		agentId = getStringParameter(filterConfig, INIT_PARAM_AGENT_ID, "");
		publicKey = getStringParameter(filterConfig, INIT_PARAM_PUBLIC_KEY, "");
		homepage = getStringParameter(filterConfig, INIT_PARAM_HOMEPAGE, "/");
		hiidoLoginUrl = getStringParameter(filterConfig, INIT_PARAM_HIIDO_LOGIN_URL, HiidoKit.HIIDO_LOGIN_URL);

		callback = getOptionBean(filterConfig, Callback.class);
		if (callback == null) {
			callback = new GenericHiidoauthCallback();
			logger.info("Use default HiidoKit.Callback to " + this.getClass().getSimpleName() + "$" + filterConfig.getFilterName());
		}

		actions.put(getRealPath(servletPathPrefix, HiidoKit.LOOKUP_PATH_POST_HIIDO_LOGIN), new ServletMethodObject() {
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
				callback.postHiidoLogin(request, response);
			}
		});

		// store cookieName
		filterConfig.getServletContext().setAttribute(Wsid.COOKIE_NAME, cookieName);

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

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
			redirectToLoginPage(request, response, servletPathPrefix);
			return;
		} else if (!wsid.validate(Webc.DEFAULT_CSRF_SECRET_BYTES, Long.MAX_VALUE)) {
			Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0); // remove
			Kits.sendError(response, Webc.ERRNO_MISSING_VERIFIER, "Missing verifier!");
			return;
		}
		Kits.writeCookie(response, cookieName, wsid.resetToken(Webc.DEFAULT_CSRF_SECRET_BYTES).toHexString(), Wsid.COOKIE_TEMPORY_EXPIRE);
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

	protected void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response, String servletPathPrefix) throws ServletException, IOException {
		Kits.sendRedirect(response, hiidoLoginUrl);
	}
}
