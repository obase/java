package com.github.obase.mysql.stmt;

import java.util.List;

import com.github.obase.mysql.core.Fragment;

/**
 * 当param为null为static, 当param非null为dynamic
 */
public class Whenany extends Container {

	public Whenany(List<Fragment> fragments) {
		super(fragments);
	}

	@Override
	protected int satisfy(int[] codes) {
		// 只有全部为
		boolean markno = false;
		for (int code : codes) {
			if (code == Pack.CODE_UKW) {
				continue;
			} else if (code == Pack.CODE_YES) {
				return Pack.CODE_YES;
			} else {
				markno = true;
			}
		}
		// 没有no也当作yes
		return markno ? Pack.CODE_NO : Pack.CODE_YES;
	}

}
