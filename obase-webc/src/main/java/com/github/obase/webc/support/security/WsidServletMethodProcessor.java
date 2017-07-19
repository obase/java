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

import static com.github.obase.webc.Webc.*;

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
			wsidTokenBase = DEFAULT_WSID_TOKEN_BASE;
		}
		timeoutMillis = params.timeoutSecond * 1000;
		if (timeoutMillis == 0) {
			timeoutMillis = DEFAULT_TIMEOUT_SECOND * 1000;
		}
		defaultAuthType = params.defaultAuthType;
		if (defaultAuthType == null) {
			defaultAuthType = DEFAULT_AUTH_TYPE;
		}
		if (params.refererDomain != null) {
			Collections.addAll(refererDomainSet, StringKit.split(params.refererDomain, COMMA, true));
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

		// step1: load principal of this request
		Principal principal = (Principal) request.getAttribute(Webc.ATTR_PRINCIPAL);
		if (principal == null) {
			// step1.1: parse wsid from request cookie
			String tk = Kits.readCookie(request, Wsid.COOKIE_NAME);
			Wsid wsid;
			if (tk == null || (wsid = Wsid.decode(tk)) == null) {
				wsid = tryOssLogin(request, response);
				if (wsid == null) {
					redirectLoginPage(request, response);
					return null;
				}
			}

			// step1.2: check csrf
			if (object.annotation.csrf()) {
				if (!wsid.validate(wsidTokenBase, timeoutMillis)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Wsid validate fail:" + Jsons.writeAsString(wsid));
					}
					Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0);// 转发前销毁cookie
					redirectLoginPage(request, response);
					return null;
				}

				String refererDomain = Util.extractDomainFromUrl(Kits.getReferer(request), false);
				if (refererDomain == null || (!refererDomain.equals(Util.extractDomainFromUrl(Kits.getHost(request), false)) && !refererDomainSet.contains(refererDomain))) {
					if (logger.isDebugEnabled()) {
						logger.debug("Referer validate fail:" + refererDomain);
					}
					sendError(response, SC_INVALID_ACCESS, SC_INVALID_ACCESS, "Invalid access!");
					return null;
				}
			}

			// step1.3: validate and extend principal timeout
			principal = validateAndExtendPrincipal(wsid);
			if (principal == null) {
				Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0);// 转发前销毁cookie
				redirectLoginPage(request, response);
				return null;
			}

			request.setAttribute(ATTR_WSID, wsid);
			request.setAttribute(ATTR_PRINCIPAL, principal);
			Kits.writeCookie(response, Wsid.COOKIE_NAME, Wsid.encode(wsid.resetToken(wsidTokenBase)), Wsid.COOKIE_TEMPORY_EXPIRE);
		}
		// step2: check permission
		if (object.authType == AuthType.PERMISSION) {
			if (!validatePermission(principal, Kits.getHttpMethod(request), object)) {
				sendError(response, SC_PERMISSION_DENIED, SC_PERMISSION_DENIED, "Permission denied!");
				return null;
			}
		}

		return request;
	}

	/**
	 * Override for subclass
	 */
	protected Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return null;
	}

	/**
	 * 验证并延长会话时间
	 */
	protected abstract Principal validateAndExtendPrincipal(Wsid wsid) throws IOException;

	protected abstract boolean validatePermission(Principal principal, HttpMethod method, ServletMethodObject object) throws IOException;

	protected abstract void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
