package com.github.obase.webc;

import com.github.obase.webc.annotation.ServletMethod;

/**
 * 保存ServletMethod的关系规则
 */
public class ServletMethodObject {

	public final String lookupPath;
	public final ServletMethodHandler handler;

	public final AuthType auth;
	public final boolean csrf; // check csrf
	public final String api; // name of the api, if not empty, it will export as api
	public final String category; // category of the api
	public final String remark; // summary to the api

	public ServletMethodObject(String lookupPath, ServletMethodHandler handler, ServletMethod annotation, AuthType defaultAuthType) {
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

}
