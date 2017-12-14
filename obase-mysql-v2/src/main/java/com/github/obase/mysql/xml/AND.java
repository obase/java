package com.github.obase.mysql.xml;

public class AND extends X {

	@Override
	protected String prefix(int idx) {
		return idx > 0 ? "AND (" : "(";
	}

	@Override
	protected String suffix() {
		return ")";
	}

}
