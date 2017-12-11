package com.github.obase.mysql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.PstmtMeta;

/**
 * 用于动态SQL的元数据辅助类
 */
public final class DPstmtMeta extends PstmtMeta {

	public final ParamBuilder params;

	// 构造时必须复制外来参数param
	public DPstmtMeta(boolean nop, String psql, ParamBuilder params) {
		super(nop, psql);
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(2048).append(psql).append(", [");
		for (int i = 0; i < params.len; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(params.name[i]);
			if (params.set[i] != 0) {
				if (params.set[i] > 0) {
					sb.append(':').append(params.set[i]);
				}
				sb.append('!');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public int setParam(PreparedStatement ps, JdbcMeta meta, Object bean) throws SQLException {
		int pos = 0;
		for (int i = 0; i < params.len; i++) {
			++pos;
			if (params.set[i] != 0) {
				JdbcMeta.setParamByType(ps, pos, params.val[i]);
			} else {
				meta.setParam(ps, pos, bean, params.name[i]);
			}
		}
		return pos;
	}

}
