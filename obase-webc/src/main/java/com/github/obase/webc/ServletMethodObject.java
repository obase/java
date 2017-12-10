package com.github.obase.webc;

import org.springframework.http.HttpMethod;

import com.github.obase.webc.annotation.ServletMethod;

/**
 * 保存ServletMethod的关系规则
 */
public class ServletMethodObject {

	public final HttpMethod method;
	public final String lookupPath;
	public final ServletMethodHandler handler;

	public final AuthType auth;
	public final boolean csrf; // check csrf
	public final String api; // name of the api, if not empty, it will export as api
	public final String category; // category of the api
	public final String remark; // summary to the api

	public ServletMethodObject(HttpMethod method, String lookupPath, ServletMethodHandler handler, ServletMethod annotation, AuthType defaultAuthType) {
		this.method = method;
		this.lookupPath = lookupPath;
		this.handler = handler;
		if (annotation != null) {
			this.auth = replaceDefault(annotation.auth(), defaultAuthType);
			this.csrf = annotation.csrf();
			this.api = annotation.api();
			this.category = annotation.category();
			this.remark = annotation.remark();
		} else {
			this.auth = null;
			this.csrf = false;
			this.api = null;
			this.category = null;
			this.remark = null;
		}
	}

	private AuthType replaceDefault(AuthType authType, AuthType defaultAuthType) {
		return authType != AuthType.DEFAULT ? authType : defaultAuthType;
	}

	@Override
	public int hashCode() {
		int result = 31;
		result = 31 * result + method.hashCode();
		result = 31 * result + lookupPath.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ServletMethodObject)) {
			return false;
		}
		ServletMethodObject that = (ServletMethodObject) obj;
		return this.method == that.method && this.lookupPath.equals(that.lookupPath);
	}

}
