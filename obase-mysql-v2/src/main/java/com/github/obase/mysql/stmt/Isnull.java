package com.github.obase.mysql.stmt;

import java.util.List;

import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;

/**
 * 当param为null为static, 当param非null为dynamic
 */
public class Isnull implements Fragment {

	final String psql;
	final String param;

	public Isnull(String psql, String param) {
		this.psql = psql;
		this.param = param;
	}

	@Override
	public boolean isDynamic() {
		return this.param != null;
	}

	@Override
	public Pack satisfy(JdbcMeta meta, Object bean) {
		if (bean == null) {
			return Pack.NO;
		}
		Object val = meta.getValue(bean, param);
		return new Pack(val == null ? Pack.CODE_YES : Pack.CODE_NO, val);
	}

	@Override
	public void processStatic(StringBuilder psqls, List<String> params) {
		psqls.append(psql);
		// 参数为null不需添加
	}

	@Override
	public void processDynamic(StringBuilder psqls, List<Param> params, Object value) {
		psqls.append(psql);
		params.add(new Param(param, value));
	}

}
