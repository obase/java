package com.github.obase.mysql.stmt;

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
	public final boolean nop;
	public final PstmtMeta staticPstmtMeta; // 缓存属性

	public Statement(String id, boolean nop, List<Fragment> fragments) {
		super(fragments);
		this.id = id;
		this.nop = nop;
		this.staticPstmtMeta = dynamic ? null : PstmtMeta.getInstance(psql, param);
	}

	@Override
	protected int satisfy(int[] codes) {
		return Pack.CODE_YES;
	}

	public PstmtMeta dynamicPstmtMeta(JdbcMeta meta, Object bean) {
		StringBuilder psqls = new StringBuilder(4096);
		List<Param> params = new LinkedList<Param>();
		processDynamic(psqls, params, satisfy(meta, bean).value);
		return new PstmtMeta(psqls.toString(), params);
	}

}
