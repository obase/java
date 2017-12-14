package com.github.obase.mysql.xml;

public class OR extends X {

	@Override
	protected String prefix(boolean appended) {
		return appended ? "OR (" : "(";
	}

	@Override
	protected String suffix() {
		return ")";
	}
}
