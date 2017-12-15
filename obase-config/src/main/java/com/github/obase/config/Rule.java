package com.github.obase.config;

import org.springframework.util.StringUtils;

class Rule {
	public final String name;
	public final boolean required;
	public final String default_;
	public final boolean crypted;
	public final String passwd;

	Rule(String name, String type, String required, String default_, String crypted, String passwd) {
		this.name = name;
		this.required = StringUtils.isEmpty(required) ? false : Boolean.parseBoolean(required);
		this.default_ = default_;
		this.crypted = StringUtils.isEmpty(crypted) ? false : Boolean.parseBoolean(crypted);
		this.passwd = passwd;
	}
}
