package com.github.obase.mysql.stmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;

public final class Cdata implements Fragment {

	public final String psql;
	public final List<String> param;

	// 构造时必须复制外来参数param
	public Cdata(String psql, List<String> param) {
		this.psql = psql;
		this.param = param == null ? Collections.emptyList() : new ArrayList<String>(param); // 必须复制
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public Pack satisfy(JdbcMeta meta, Object bean) {
		return Pack.UKW;
	}

	@Override
	public void processStatic(StringBuilder psqls, List<String> params) {
		psqls.append(psql);
		params.addAll(param);
	}

	@Override
	public void processDynamic(StringBuilder psqls, List<Param> params, Object value) {
		psqls.append(psql);
		for (String p : param) {
			params.add(new Param(p));
		}
	}

}
