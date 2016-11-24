package com.github.obase.config;

import java.util.LinkedList;
import java.util.List;

class Rules {

	public final String passwd;
	public final List<Rule> rules = new LinkedList<Rule>();

	Rules(String passwd) {
		this.passwd = passwd;
	}

}
