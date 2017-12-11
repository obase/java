package com.github.obase.webc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServletMethodHandler {

	protected Object bean;
	protected ServletMethodFilter[] filters;

	public final ServletMethodHandler bind(Object bean, ServletMethodFilter... filters) {
		this.bean = bean;
		this.filters = filters;
		return this;
	}

	public abstract void service(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
