package com.github.obase.webc;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServletMethodHandler {

	protected Object bean;
	protected ServletMethodFilter[] filters;

	protected final ServletMethodHandler bind(Object bean, ServletMethodFilter... filters) {
		this.bean = bean;
		this.filters = filters;
		return this;
	}

	public abstract void service(HttpServletRequest request, HttpServletResponse response) throws Exception;

	public final String toString() {
		return new StringBuilder(512).append("{bean:").append(bean == null ? null : bean.getClass().getCanonicalName()).append(",filters:").append(filters == null ? null : Arrays.toString(filters)).append("}").toString();
	}

}
