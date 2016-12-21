package com.github.obase.mysql.demo;

import java.io.Serializable;
import java.util.Set;

import com.github.obase.mysql.annotation.Meta;

@Meta
public class Session implements Serializable {

	private static final long serialVersionUID = 1L;

	String user;
	Set<String> roles;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

}
