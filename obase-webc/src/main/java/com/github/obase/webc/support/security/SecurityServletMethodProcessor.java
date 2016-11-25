package com.github.obase.webc.support.security;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.AuthType;
import com.github.obase.webc.Webc.Util;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.support.BaseServletMethodProcessor;
import com.github.obase.kit.StringKit;

/**
 * Security base implementation
 */
public abstract class SecurityServletMethodProcessor extends BaseServletMethodProcessor {

	protected final Set<String> csrfRefererDomain = new HashSet<String>();
	protected byte[] csrfSecretBytes = Webc.DEFAULT_CSRF_SECRET_BYTES;
	protected AuthType defaultAuthType = AuthType.PERMISSION;

	public final void setCsrfRefererDomain(String csrfRefererDomain) {
		this.csrfRefererDomain.clear();
		if (StringKit.isNotEmpty(csrfRefererDomain)) {
			Collections.addAll(this.csrfRefererDomain, StringKit.split(csrfRefererDomain, ',', true));
		}
	}

	public final void setCsrfSecretBytes(String csrfSecretBytes) {
		this.csrfSecretBytes = csrfSecretBytes.getBytes();
	}

	public final void setDefaultAuthType(AuthType defaultAuthType) {
		this.defaultAuthType = defaultAuthType;
	}

	@Override
	public final HttpServletRequest preprocess(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Exception {

		ServletMethod annotation = object.annotation;
		/* if dynamic add from initMappingRules() will be null. */
		AuthType authType = annotation == null ? AuthType.NONE : (annotation.auth() == AuthType.DEFAULT ? this.defaultAuthType : annotation.auth());

		// step0: exclude
		if (authType == AuthType.NONE) {
			return request;
		}

		// step1: read session id, if null forward to login page
		Wsid wsid = Wsid.decode(Kits.readCookie(request, Wsid.COOKIE_NAME));
		if (wsid == null) {
			wsid = tryOssLogin(request, response);
			if (wsid == null) {
				redirectToLoginPage(request, response);
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("OSS login:" + Kits.readCookieMap(request));
			}
		} else if (!wsid.validate(csrfSecretBytes, sessionTimeout)) {
			Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0); // remove
			if (sendError) {
				Kits.sendError(response, Webc.ERRNO_MISSING_VERIFIER, "Missing verifier!");
			} else {
				Kits.writeErrorMessage(response, Webc.ERRNO_MISSING_VERIFIER, Webc.ERRNO_MISSING_VERIFIER, "Missing verifier!");
			}
			return null;
		}
		request.setAttribute(Webc.ATTR_WSID, wsid);

		// step2: check csrf
		if (annotation.csrf()) {
			String refererDomain = Util.extractDomainFromUrl(Kits.getReferer(request), false);
			if (!this.csrfRefererDomain.contains(refererDomain) && !StringKit.equals(refererDomain, Util.extractDomainFromUrl(Kits.getHost(request), false))) {
				Kits.writeCookie(response, Wsid.COOKIE_NAME, "", 0); // remove
				if (logger.isDebugEnabled()) {
					logger.debug("Csrf error:" + Kits.getReferer(request) + ", " + wsid.toHexString());
				}
				if (sendError) {
					Kits.sendError(response, Webc.ERRNO_CSRF_ERROR, "CSRF error!");
				} else {
					Kits.writeErrorMessage(response, Webc.ERRNO_CSRF_ERROR, Webc.ERRNO_CSRF_ERROR, "CSRF error!");
				}
				return null;
			}
		}

		// step3: get session, if null forward to login page
		Principal principal = validatePrincipal(request, response, wsid);
		if (principal == null) {
			redirectToLoginPage(request, response);
			return null;
		}
		request.setAttribute(Webc.ATTR_PRINCIPAL, principal);

		// step4: check permission
		if (authType == AuthType.PERMISSION) {
			if (!validatePermission(principal, annotation, Kits.getLookupPath(request))) {
				if (sendError) {
					Kits.sendError(response, Webc.ERRNO_PERMISSION_DENIED, "Permission denied!");
				} else {
					Kits.writeErrorMessage(response, Webc.ERRNO_PERMISSION_DENIED, Webc.ERRNO_PERMISSION_DENIED, "Permission denied!");
				}
				return null;
			}
		}

		return request;
	}

	@Override
	public void postprocess(HttpServletRequest request, HttpServletResponse response, Throwable t) {
		super.postprocess(request, response, t);
		Wsid wsid = Kits.getWsid(request);
		if (wsid != null) {
			Kits.writeCookie(response, Wsid.COOKIE_NAME, wsid.resetToken(this.csrfSecretBytes).toHexString(), Wsid.COOKIE_TEMPORY_EXPIRE);
		}
	}

	protected abstract Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	protected abstract Principal validatePrincipal(HttpServletRequest request, HttpServletResponse response, Wsid wsid) throws IOException;

	protected abstract boolean validatePermission(Principal principal, ServletMethod annotation, String lookupPath) throws IOException;

	protected abstract void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
