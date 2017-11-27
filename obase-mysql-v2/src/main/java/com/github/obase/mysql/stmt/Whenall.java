package com.github.obase.mysql.stmt;

import java.util.List;

import com.github.obase.mysql.core.Fragment;

/**
 * 当param为null为static, 当param非null为dynamic
 */
public class Whenall extends Container {

	public Whenall(List<Fragment> fragments) {
		super(fragments);
	}

	@Override
	protected int satisfy(int[] codes) {
		for (int code : codes) {
			if (code == Pack.UKW) {
				continue;
			} else if (code == Pack.NO) {
				return Pack.NO;
			}
		}
		return Pack.YES;
	}

}
