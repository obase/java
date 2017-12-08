package com.github.obase.mysql.stmt;

import com.github.obase.mysql.core.DLink;
import com.github.obase.mysql.core.DNode;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.Part;
import com.github.obase.mysql.core.PstmtMeta;

/**
 * 语句Union结构,根据dynamic区分是动态还是静态
 */
public class Statement {

	public final String id;
	public final boolean nop;// 如果select或from子句包含参数,请设置nop为true.
	public final PstmtMeta staticPstmtMeta; // 静态PstmtMeta
	public final DLink<Part> dynamicChildren;

	public Statement(String id, boolean nop, DLink<Part> link) {

		this.id = id;
		this.nop = nop;

		DNode<Part> dn = link.head;
		if (dn.next == null) {
			this.staticPstmtMeta = new PstmtMeta(dn.value.getPsql(), dn.value.getParams());
			this.dynamicChildren = DLink.nil();
		} else {
			this.staticPstmtMeta = null;
			this.dynamicChildren = link;
		}
	}

	public PstmtMeta dynamicPstmtMeta(JdbcMeta meta, Object bean) {
		DLink<String> psqls = new DLink<String>();
		DLink<Param> params = new DLink<Param>();

		int idx = 0;
		for (DNode<Part> t = dynamicChildren.head; t != null; t = t.next) {
			Part p = t.value;
			if (p.isDynamic()) {
				p.processDynamic(meta, bean, psqls, params, idx++);
			} else {
				psqls.tail(p.getPsql()); // 经过parser自动去掉了空的元素
				params.tail(p.getParams());
			}
		}

		StringBuilder sb = new StringBuilder(4096);
		for (DNode<String> t = psqls.head; t != null; t = t.next) {
			sb.append(t.value);
		}
		return new PstmtMeta(psqls.toString(), params);
	}

}
