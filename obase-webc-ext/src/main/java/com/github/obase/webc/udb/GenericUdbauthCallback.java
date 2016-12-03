package com.github.obase.webc.udb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duowan.udb.auth.UserinfoForOauth;
import com.github.obase.webc.Kits;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.support.security.Principal;
import com.github.obase.webc.support.security.SimplePrincipal;
import com.github.obase.webc.udb.UdbKit.Callback;

public class GenericUdbauthCallback implements Callback {

	@Override
	public boolean postUdbLogin(HttpServletRequest request, HttpServletResponse response, String yyuid, String[] uProfile) throws IOException {
		Wsid wsid = Wsid.valueOf(yyuid + Webc.COMMA + uProfile[0]).resetToken(Webc.DEFAULT_CSRF_SECRET_BYTES);
		String cookieName = (String) request.getServletContext().getAttribute(Wsid.COOKIE_NAME);
		Kits.writeCookie(response, cookieName == null ? Wsid.COOKIE_NAME : cookieName, wsid.toHexString(), Wsid.COOKIE_TEMPORY_EXPIRE);
		return true;
	}

	@Override
	public void preUdbLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String cookieName = (String) request.getServletContext().getAttribute(Wsid.COOKIE_NAME);
		Kits.writeCookie(response, cookieName == null ? Wsid.COOKIE_NAME : cookieName, "", 0);
	}

	@Override
	public Principal validatePrincipal(HttpServletRequest request, HttpServletResponse response, Wsid wsid) throws IOException {

		SimplePrincipal principal = new SimplePrincipal();

		String str = new String(wsid.id);
		int pos = str.indexOf(Webc.COMMA);
		if (pos == -1) {
			principal.setYyuid(str);
		} else {
			principal.setYyuid(str.substring(0, pos));
			principal.setPassport(str.substring(pos + 1));
		}
		return principal;
	}

	@Override
	public void sendBadParameterError(HttpServletResponse response, int errno, String errmsg) throws IOException {
		Kits.sendError(response, errno, errmsg);
	}

	@Override
	public Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response, String appid, String appkey) throws IOException {
		UserinfoForOauth userinfoForOauth = new UserinfoForOauth(request, response, appid, appkey);
		if (userinfoForOauth.validate()) {
			@SuppressWarnings("deprecation")
			String username = userinfoForOauth.getUsername();
			String yyuid = userinfoForOauth.getYyuid();

			Wsid wsid = Wsid.valueOf(yyuid + Webc.COMMA + username).resetToken(Webc.DEFAULT_CSRF_SECRET_BYTES);
			return wsid;
		}
		return null;
	}

}
