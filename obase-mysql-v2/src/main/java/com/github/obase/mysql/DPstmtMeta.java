package com.github.obase.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.obase.mysql.xml.Param;

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

	@Override
	public int setParam(PreparedStatement ps, JdbcMeta meta, Object bean) throws SQLException {
		int pos = 0;
		for (DNode<Param> t = phead; t != null; t = t.next) {
			Param p = t.value;
			++pos;
			if (p.set) {
				JdbcMeta.setParamByType(ps, pos, p.val);
			} else {
				meta.setParam(ps, pos, bean, p.name);
			}
		}
		return pos;
	}

}
