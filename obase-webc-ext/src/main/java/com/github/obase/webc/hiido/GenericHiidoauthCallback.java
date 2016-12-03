package com.github.obase.webc.hiido;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.Wsid;
import com.github.obase.webc.hiido.HiidoKit.Callback;
import com.github.obase.webc.support.security.Principal;

public class GenericHiidoauthCallback implements Callback {

	@Override
	public void postHiidoLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Principal validatePrincipal(HttpServletRequest request, HttpServletResponse response, Wsid wsid) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
