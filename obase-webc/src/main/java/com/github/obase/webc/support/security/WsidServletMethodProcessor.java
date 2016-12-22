package com.github.obase.webc.support.security;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import com.github.obase.json.Jsons;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.AuthType;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Webc.Util;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.security.Principal;
import com.github.obase.webc.support.BaseServletMethodProcessor;

/**
 * Security base implementation
 */
public abstract class WsidServletMethodProcessor extends BaseServletMethodProcessor {

	protected int wsidTokenBase;
	protected long timeoutMillis;
	protected AuthType defaultAuthType;
	protected final Set<String> refererDomainSet = new HashSet<String>();

	@Override
	public void setup(FilterInitParam params, Map<String, ServletMethodObject> rules) throws ServletException {
		super.setup(params, rules);

		wsidTokenBase = params.wsidTokenBase;
		if (wsidTokenBase == 0) {
			wsidTokenBase = Webc.DEFAULT_WSID_TOKEN_BASE;
		}
		timeoutMillis = params.timeoutSecond * 1000;
		if (timeoutMillis == 0) {
			timeoutMillis = Webc.DEFAULT_TIMEOUT_SECOND * 1000;
		}
		defaultAuthType = params.defaultAuthType;
		if (defaultAuthType == null) {
			defaultAuthType = Webc.DEFAULT_AUTH_TYPE;
		}
		if (params.refererDomain != null) {
			Collections.addAll(refererDomainSet, StringKit.split(params.refererDomain, Webc.COMMA, true));
		}

		// set object authType
		for (ServletMethodObject object : rules.values()) {
			if (object.annotation != null) {
				object.authType = object.annotation.auth();
				if (object.authType == AuthType.DEFAULT) {
					object.authType = defaultAuthType;
				}
			} else {
				object.authType = AuthType.NONE;
			}
		}
	}

	@Override
	public final HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Throwable {

		// step0: exclude
		if (object.authType == AuthType.NONE) {
			return request;
		}

		// step1: read session id, if null forward to login page
		Wsid wsid = Wsid.decode(Kits.readCookie(request, Wsid.COOKIE_NAME));
		if (wsid == null) {
			wsid = tryOssLogin(request);
			if (wsid == null) {
				redirectLoginPage(request, response);
				return null;
			}
		}
		request.setAttribute(Webc.ATTR_WSID, wsid);

		// step2: check csrf
		if (object.annotation.csrf()) {
			if (!wsid.validate(wsidTokenBase, timeoutMillis)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Wsid validate fail:" + Jsons.writeAsString(wsid));
				}
				redirectLoginPage(request, response);
				return null;
			} else {
				wsid.resetToken(wsidTokenBase);
				Kits.writeCookie(response, Wsid.COOKIE_NAME, Wsid.encode(wsid), Wsid.COOKIE_TEMPORY_EXPIRE);
			}

			String refererDomain = Util.extractDomainFromUrl(Kits.getReferer(request), false);
			if (refererDomain == null || (!refererDomain.equals(Util.extractDomainFromUrl(Kits.getHost(request), false)) && !refererDomainSet.contains(refererDomain))) {
				if (logger.isDebugEnabled()) {
					logger.debug("Referer validate fail:" + refererDomain);
				}
				redirectLoginPage(request, response);
				return null;
			}
		}

		// step3: check session, if null forward to login page
		Principal principal = validateAndExtendPrincipal(wsid);
		if (principal == null) {
			redirectLoginPage(request, response);
			return null;
		}
		request.setAttribute(Webc.ATTR_PRINCIPAL, principal);

		// step4: check permission
		if (object.authType == AuthType.PERMISSION) {
			if (!validatePermission(principal, Kits.getHttpMethod(request), Kits.getLookupPath(request))) {
				sendError(response, Webc.SC_PERMISSION_DENIED, Webc.SC_PERMISSION_DENIED, "Permission denied!");
				return null;
			}
		}

		return request;
	}

	/**
	 * Override for subclass
	 */
	protected Wsid tryOssLogin(HttpServletRequest request) throws ServletException, IOException {
		return null;
	}

	/**
	 * 验证并延长会话时间
	 */
	protected abstract Principal validateAndExtendPrincipal(Wsid wsid) throws IOException;

	protected abstract boolean validatePermission(Principal principal, HttpMethod method, String lookupPath) throws IOException;

	protected abstract void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
