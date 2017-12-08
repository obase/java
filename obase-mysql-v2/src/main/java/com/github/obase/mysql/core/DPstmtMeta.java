package com.github.obase.mysql.core;

import com.github.obase.mysql.stmt.Param;

/**
 * 用于动态SQL的元数据辅助类
 */
public final class DPstmtMeta extends PstmtMeta {

	public final DNode<Param> phead;

	// 构造时必须复制外来参数param
	public DPstmtMeta(boolean nop, String psql, DNode<Param> phead) {
		super(nop, psql);
		this.phead = phead;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(4096).append(psql).append(", [");
		for (DNode<Param> t = phead; t != null; t = t.next) {
			if (t != phead) {
				sb.append(',');
			}
			sb.append(t.value);
		}
		sb.append(']');
		return sb.toString();
	}

}
