package com.github.obase.mysql.stmt;

public class OR extends X {

	@Override
	protected String prefix(int idx) {
		return idx == 0 ? "(" : " OR (";
	}

	@Override
	protected String suffix() {
		return ")";
	}
}
