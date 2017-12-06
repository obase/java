package com.github.obase.mysql.stmt;

import java.util.List;

import com.github.obase.mysql.core.Fragment;

public class OR extends Generic {

	public OR(String s, List<Fragment> children) {
		super(true, s, children);
	}

	@Override
	protected String prefix(int idx) {
		return idx == 0 ? "" : "OR ";
	}

}
