package com.github.obase.mysql.stmt;

import java.util.List;

import com.github.obase.mysql.core.Fragment;

public class WHERE extends Dynamic {

	public WHERE(String s, List<Fragment> children) {
		super(s, children);
	}

	@Override
	protected String prefix(int idx) {
		return "WHERE ";
	}

}
