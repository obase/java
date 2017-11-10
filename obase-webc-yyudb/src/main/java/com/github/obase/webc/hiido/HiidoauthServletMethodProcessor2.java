package com.github.obase.webc.hiido;

import static com.github.obase.webc.Webc.SC_INVALID_ACCOUNT;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.kit.ObjectKit;
import com.github.obase.kit.StringKit;
import com.github.obase.security.Principal;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodHandler;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.webc.support.security.WsidServletMethodProcessor;
import com.github.obase.webc.yy.UserPrincipal;

/**
 * Used to instead of HiidoauthServletMethodProcessor
 */
public abstract class HiidoauthServletMethodProcessor2 extends WsidServletMethodProcessor {

	protected abstract String getUdbApi();

	protected abstract String getAgentId();

	protected abstract byte[] getAgentPwd();

	protected abstract String getPublicKey();

	protected abstract String getHomepage();

	protected abstract String getHiidoLoginUrl();

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
				Kits.sendRedirect(response, Kits.getServletPath(request, ObjectKit.<String>ifnull(getHomepage(), "/")));
			}
		};

		ServletMethodObject object = new ServletMethodObject(null, HiidoKit.LOOKUP_PATH_POST_HIIDO_LOGIN);
		Arrays.fill(object.handlers, postHiidoLoginObject);
		rules.put(HiidoKit.LOOKUP_PATH_POST_HIIDO_LOGIN, object);

		super.setup(params, rules);
	}

	public boolean postHiidoLogin(HttpServletRequest request, HttpServletResponse response, String token) throws ServletException, IOException {

		if (StringKit.isEmpty(token)) {
			return false;
		}
		Principal principal = validatePrincipal(HiidoKit.getStaffInfoByToken(ObjectKit.ifnull(getUdbApi(), HiidoKit.HIIDO_UDB_API), getAgentId(), getAgentPwd(), getPublicKey(), token));
		if (principal == null) {
			return false;
		}

		Wsid wsid = Wsid.valueOf(principal.getKey()).resetToken(wsidTokenBase); // csrf
		getWsidSession().passivate(wsid.id, encodePrincipal(principal), timeoutMillis);

		request.setAttribute(Webc.ATTR_WSID, wsid);
		request.setAttribute(Webc.ATTR_PRINCIPAL, principal);
		Kits.writeCookie(response, wsidName, Wsid.encode(wsid), wsidDomain, Wsid.COOKIE_PATH, Wsid.COOKIE_TEMPORY_EXPIRE);

		return true;
	}

	@Override
	protected void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Kits.sendRedirect(response, ObjectKit.<String>ifnull(getHiidoLoginUrl(), HiidoKit.HIIDO_LOGIN_URL));
	}

	// for subclass override
	protected Principal validatePrincipal(UserPrincipal staffInfoByToken) {
		return staffInfoByToken;
	}

	public final List<Principal> getMyAgentStaffInfo() {
		return HiidoKit.getMyAgentStaffInfo(ObjectKit.ifnull(getUdbApi(), HiidoKit.HIIDO_UDB_API), getAgentId(), getAgentPwd(), getPublicKey());
	}

	public void updateMyStaffAgentInfo(boolean valid, String... users) {
		HiidoKit.updateMyStaffAgentInfo(ObjectKit.ifnull(getUdbApi(), HiidoKit.HIIDO_UDB_API), getAgentId(), getAgentPwd(), getPublicKey(), valid, users);
	}

	public void updateMyStaffAgentInfo(Map<String, Boolean> users) {
		HiidoKit.updateMyStaffAgentInfo(ObjectKit.ifnull(getUdbApi(), HiidoKit.HIIDO_UDB_API), getAgentId(), getAgentPwd(), getPublicKey(), users);
	}

	@Override
	protected String encodePrincipal(Principal p) {
		return ((UserPrincipal) p).encode();
	}

	@Override
	protected Principal decodePrincipal(String v) {
		return v == null ? null : new UserPrincipal().decode(v);
	}
}
