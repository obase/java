package com.github.obase.webc;

import org.springframework.http.HttpMethod;

import com.github.obase.webc.annotation.ServletMethod;

/**
 * 保存ServletMethod的关系规则
 */
public class ServletMethodRules {

	public static final int SIZE = HttpMethod.values().length;

	public final String lookupPath;
	public final ServletMethodObject[] objects = new ServletMethodObject[SIZE];
	public final ServletMethod[] annotations = new ServletMethod[SIZE];

	public ServletMethodRules(String lookupPath) {
		this.lookupPath = lookupPath;
	}

}
