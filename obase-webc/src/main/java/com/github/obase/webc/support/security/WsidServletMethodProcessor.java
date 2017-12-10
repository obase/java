package com.github.obase.webc.support.security;

import static com.github.obase.webc.Webc.ATTR_PRINCIPAL;
import static com.github.obase.webc.Webc.ATTR_WSID;
import static com.github.obase.webc.Webc.COMMA;
import static com.github.obase.webc.Webc.DEFAULT_TIMEOUT_SECOND;
import static com.github.obase.webc.Webc.DEFAULT_WSID_TOKEN_BASE;
import static com.github.obase.webc.Webc.SC_INVALID_ACCESS;
import static com.github.obase.webc.Webc.SC_PERMISSION_DENIED;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.json.Jsons;
import com.github.obase.kit.StringKit;
import com.github.obase.security.Principal;
import com.github.obase.webc.AuthType;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Webc.Util;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.WsidSession;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.webc.support.BaseServletMethodProcessor;

/**
 * Security base implementation
 */
public abstract class WsidServletMethodProcessor extends BaseServletMethodProcessor {

	protected int wsidTokenBase;
	protected String wsidDomain;
	protected String wsidName;
	protected long timeoutMillis;
	protected final Set<String> refererDomainSet = new HashSet<String>();

	@Override
	public void init(FilterInitParam params) {
		super.init(params);
		wsidTokenBase = params.wsidTokenBase;
		if (wsidTokenBase == 0) {
			wsidTokenBase = DEFAULT_WSID_TOKEN_BASE;
		}
		timeoutMillis = params.timeoutSecond * 1000;
		if (timeoutMillis == 0) {
			timeoutMillis = DEFAULT_TIMEOUT_SECOND * 1000;
		}
		if (params.refererDomain != null) {
			refererDomainSet.addAll(StringKit.split2List(params.refererDomain, COMMA, true));
		}
		if (params.wsidDomain != null) {
			wsidDomain = params.wsidDomain;
		}
		if (params.wsidName != null) {
			wsidName = params.wsidName;
		} else {
			wsidName = Wsid.COOKIE_NAME;
		}
	}

	@Override
	public final HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Throwable {

		// step0: exclude
		if (object.auth == AuthType.NONE) {
			return request;
		}

		// step1: load principal of this request
		Principal principal = (Principal) request.getAttribute(Webc.ATTR_PRINCIPAL);
		if (principal == null) {
			// step1.1: parse wsid from request cookie
			String tk = Kits.readCookie(request, wsidName);
			Wsid wsid;
			if (tk == null || (wsid = Wsid.decode(tk)) == null) {
				wsid = tryOssLogin(request, response);
				if (wsid == null) {
					redirectLoginPage(request, response);
					return null;
				}
			}

			// step1.2: check csrf
			if (object.csrf) {
				if (!wsid.validate(wsidTokenBase, timeoutMillis)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Wsid validate fail:" + Jsons.writeAsString(wsid));
					}
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
			principal = decodePrincipal(getWsidSession().activate(wsid.id, timeoutMillis));
			if (principal == null) {
				redirectLoginPage(request, response);
				return null;
			}

			request.setAttribute(ATTR_WSID, wsid);
			request.setAttribute(ATTR_PRINCIPAL, principal);
			Kits.writeCookie(response, wsidName, Wsid.encode(wsid.resetToken(wsidTokenBase)), wsidDomain, Wsid.COOKIE_PATH, Wsid.COOKIE_TEMPORY_EXPIRE);
		}
		// step2: check permission
		if (object.auth == AuthType.PERMISSION) {
			principal = validatePermission(principal, object);
			if (principal == null) {
				sendError(response, SC_PERMISSION_DENIED, SC_PERMISSION_DENIED, "Permission denied!");
				return null;
			}
			request.setAttribute(ATTR_PRINCIPAL, principal);
		}

		return request;
	}

	/**
	 * try login via oss
	 * 
	 * Override for subclass
	 */
	protected Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return null;
	}

	/**
	 * validate perssion for auth=PERMISSION
	 * 
	 * Override for subclass
	 */
	protected Principal validatePermission(Principal principal, ServletMethodObject object) throws IOException {
		return principal;
	}

	/**
	 * get wsid session to store and load principal
	 */
	protected abstract WsidSession getWsidSession();

	protected abstract String encodePrincipal(Principal p);

	protected abstract Principal decodePrincipal(String v);

	protected abstract void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
