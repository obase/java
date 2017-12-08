package com.github.obase.mysql.stmt;

public class WHERE extends X {

	@Override
	protected String prefix(int idx) {
		return "WHERE ";
	}

}
