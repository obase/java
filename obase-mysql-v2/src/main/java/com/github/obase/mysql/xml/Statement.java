package com.github.obase.mysql.xml;

import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.impl.DPstmtMeta;
import com.github.obase.mysql.impl.ParamBuilder;
import com.github.obase.mysql.impl.SPstmtMeta;

/**
 * 语句Union结构,根据dynamic区分是动态还是静态
 */
public class Statement {

	public final String id;
	public final boolean nop;// 如果select或from子句包含参数,请设置nop为true.
	public final SPstmtMeta staticPstmtMeta; // 静态PstmtMeta
	private final Part[] parts;

	public Statement(String id, boolean nop, Part[] parts) {

		this.id = id;
		this.nop = nop;

		if (parts.length == 1) {
			Part p = parts[0];
			this.staticPstmtMeta = new SPstmtMeta(this.nop, p.getPsql(), p.getParams());
			this.parts = null;
		} else {
			this.staticPstmtMeta = null;
			this.parts = parts == null ? Part.EMPTY_ARRAY : parts;
		}
	}

	public DPstmtMeta dynamicPstmtMeta(JdbcMeta meta, Object bean) {

		StringBuilder psqls = new StringBuilder(4096); // 默认4K
		ParamBuilder params = new ParamBuilder(128);

		int idx = 0;
		for (Part p : parts) {
			if (p.isDynamic()) {
				if (p.processDynamic(meta, bean, psqls, params, idx)) { // FIXBUG: statement is not dynamic element
					psqls.append(Part.SPACE);
					idx++;
				}
			} else {
				psqls.append(p.getPsql()).append(Part.SPACE);
				params.append(p.getParams());
				idx++;
			}
		}
		if (idx > 0) {
			psqls.setLength(psqls.length() - 1);
		}

		return new DPstmtMeta(this.nop, psqls.toString(), params);
	}

}
