package com.huya.dbms.component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.github.obase.security.Principal;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.support.security.WsidServletMethodProcessor;
import com.huya.dbms.model.UserPrincipal;

/**
 * 一路畅通
 */
@Component
public class DbmsServletMethodProcessor extends WsidServletMethodProcessor {

	@Override
	protected Wsid tryOssLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return Wsid.valueOf("Tester");
	}

	@Override
	protected Principal validateAndExtendPrincipal(Wsid wsid) throws IOException {
		UserPrincipal user = new UserPrincipal();
		user.setPassport("Tester");
		return user;
	}

	@Override
	protected Principal validatePermission(Principal principal, HttpMethod method, ServletMethodObject object) throws IOException {
		return principal;
	}

	@Override
	protected void redirectLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Kits.sendRedirect(response, "/");
	}

}
