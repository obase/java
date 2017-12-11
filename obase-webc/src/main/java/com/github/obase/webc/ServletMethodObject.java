package com.github.obase.webc;

import org.springframework.http.HttpMethod;

import com.github.obase.webc.annotation.ServletMethod;

/**
 * 保存ServletMethod的关系规则
 */
public final class ServletMethodObject {

	public final HttpMethod method;
	public final String lookupPath;

	public final AuthType auth;
	public final boolean csrf; // check csrf
	public final String api; // name of the api, if not empty, it will export as api
	public final String category; // category of the api
	public final String remark; // summary to the api

	public final ServletMethodHandler handler; // 处理句柄缓存,允许用户替换

	// 提供方便给processor.setup替换
	public ServletMethodObject(HttpMethod method, String lookupPath, AuthType auth, boolean csrf, String api, String category, String remark, ServletMethodHandler handler) {
		this.method = method;
		this.lookupPath = lookupPath;
		this.auth = auth;
		this.csrf = csrf;
		this.api = api;
		this.category = category;
		this.remark = remark;
		this.handler = handler;
	}

	public ServletMethodObject(HttpMethod method, String lookupPath, ServletMethod annotation, AuthType defaultAuthType, ServletMethodHandler handler) {
		this.method = method;
		this.lookupPath = lookupPath;
		if (annotation != null) {
			this.auth = annotation.auth() == AuthType.DEFAULT ? defaultAuthType : annotation.auth();
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
		this.handler = handler;
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
