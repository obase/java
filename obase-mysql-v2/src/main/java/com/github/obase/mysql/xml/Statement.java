package com.github.obase.mysql.xml;

import com.github.obase.mysql.DLink;
import com.github.obase.mysql.DNode;
import com.github.obase.mysql.DPstmtMeta;
import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.Part;
import com.github.obase.mysql.SPstmtMeta;

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
			this.staticPstmtMeta = SPstmtMeta.getInstance(this.nop, p.getPsql(), p.getParams());
			this.parts = null;
		} else {
			this.staticPstmtMeta = null;
			this.parts = parts == null ? Part.EMPTY_ARRAY : parts;
		}
	}

	public DPstmtMeta dynamicPstmtMeta(JdbcMeta meta, Object bean) {

		DLink<String> psqls = new DLink<String>();
		DLink<Param> params = new DLink<Param>();

		for (int i = 0, n = parts.length; i < n; i++) {
			Part p = parts[i];
			if (p.isDynamic()) {
				p.processDynamic(meta, bean, psqls, params, i++);
			} else {
				psqls.tail(p.getPsql()); // 经过parser自动去掉了空的元素
				params.tail(p.getParams());
			}
		}

		StringBuilder sb = new StringBuilder(4096);
		for (DNode<String> t = psqls.head; t != null; t = t.next) {
			sb.append(t.value);
		}

		return new DPstmtMeta(this.nop, sb.toString(), params.head);
	}

}
