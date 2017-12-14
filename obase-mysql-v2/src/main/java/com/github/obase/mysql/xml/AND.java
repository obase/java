package com.github.obase.mysql.xml;

public class AND extends X {

	@Override
	protected String prefix(boolean appended) {
		return appended ? "AND (" : "(";
	}

	@Override
	protected String suffix() {
		return ")";
	}

}
