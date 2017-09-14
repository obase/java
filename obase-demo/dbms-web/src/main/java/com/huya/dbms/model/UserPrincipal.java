package com.huya.dbms.model;

import com.github.obase.security.Principal;

public class UserPrincipal implements Principal {

	private static final long serialVersionUID = 1L;

	String passport;

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	@Override
	public String key() {
		return passport;
	}

}
