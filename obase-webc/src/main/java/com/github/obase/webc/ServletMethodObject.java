package com.github.obase.webc;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.annotation.ServletMethod;

public abstract class ServletMethodObject {

	public Object bean;
	public String method;
	public ServletMethod annotation;
	public ServletMethodFilter[] filters;

	protected final ServletMethodObject init(Object bean, String method, ServletMethod annotation, ServletMethodFilter... filters) {
		this.bean = bean;
		this.method = method;
		this.annotation = annotation;
		this.filters = filters;
		return this;
	}

	public abstract void service(HttpServletRequest request, HttpServletResponse response) throws Exception;

	public final String toString() {
		return new StringBuilder(512).append("{bean=").append(bean == null ? null : bean.getClass().getCanonicalName()).append(",method=").append(method).append(",annotation=").append(annotation)
				.append(",filters=").append(filters == null ? null : Arrays.toString(filters)).append("}").toString();
	}

}
