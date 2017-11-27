package com.github.obase.mysql.stmt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.PstmtMeta;

/**
 * 语句Union结构,根据dynamic区分是动态还是静态
 */
public class Statement extends Container {

	public final String id;
	private PstmtMeta pstmtMeta; // 缓存属性

	public Statement(String id, List<Fragment> fragments) {
		super(fragments);
		this.id = id;
	}

	@Override
	protected int satisfy(int[] codes) {
		return Pack.YES;
	}

	public PstmtMeta getPstmtMeta(JdbcMeta meta, Object bean) {
		if (dynamic) {
			StringBuilder psqls = new StringBuilder(4096);
			List<Param> params = new LinkedList<Param>();
			processDynamic(psqls, params, satisfy(meta, bean).value);
			return new PstmtMeta(psqls.toString(), params);
		} else {
			if (pstmtMeta == null) {
				List<Param> ps = new ArrayList<Param>(param == null ? 0 : param.size());
				for (String p : param) {
					ps.add(new Param(p));
				}
				pstmtMeta = new PstmtMeta(psql, ps);
			}
			return pstmtMeta;
		}
	}

}
