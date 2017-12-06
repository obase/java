package com.github.obase.mysql.stmt;

import java.util.LinkedList;
import java.util.List;

import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.PstmtMeta;

/**
 * 语句Union结构,根据dynamic区分是动态还是静态
 */
public class Statement extends Generic {

	public final String id;
	public final boolean nop;// 如果select或from子句包含参数,请设置nop为true.
	public final PstmtMeta staticPstmtMeta; // 静态PstmtMeta

	public Statement(String id, boolean nop, List<Fragment> children) {
		super(false, null, children);
		this.id = id;
		this.nop = nop;
		this.staticPstmtMeta = d ? null : new PstmtMeta(psql, params);
	}

	public PstmtMeta dynamicPstmtMeta(JdbcMeta meta, Object bean) {
		StringBuilder psql = new StringBuilder(4096);
		List<Param> params = new LinkedList<Param>();
		this.processDynamic(meta, bean, psql, params, 0);
		int size = params.size();
		return new PstmtMeta(psql.toString(), size > 0 ? params.toArray(new Param[size]) : Param.EMPTY_ARRAY);
	}

}
