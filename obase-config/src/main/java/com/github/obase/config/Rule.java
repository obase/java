package com.github.obase.config;

import org.springframework.util.StringUtils;

class Rule {
	public final String name;
	public final Type type;
	public final boolean required;
	public final String default_;
	public final boolean crypted;
	public final String passwd;

	Rule(String name, String type, String required, String default_, String crypted, String passwd) {
		this.name = name;
		this.type = StringUtils.isEmpty(type) ? Type.String : Type.valueOf(type);
		this.required = StringUtils.isEmpty(required) ? false : Boolean.parseBoolean(required);
		this.default_ = default_;
		this.crypted = StringUtils.isEmpty(crypted) ? false : Boolean.parseBoolean(crypted);
		this.passwd = passwd;
	}
}
