package com.github.obase.webc;

import org.springframework.http.HttpMethod;

import com.github.obase.webc.annotation.ServletMethod;

/**
 * 保存ServletMethod的关系规则
 */
public class ServletMethodObject {

	public static final int HANDLES_LENGTH = HttpMethod.values().length;

	public AuthType authType; // process default parsing
	public final ServletMethod annotation;
	public final String lookupPath;
	public final ServletMethodHandler[] handlers = new ServletMethodHandler[HANDLES_LENGTH];

	public ServletMethodObject(ServletMethod annotation, String lookupPath) {
		this.annotation = annotation;
		this.lookupPath = lookupPath;
	}

}
