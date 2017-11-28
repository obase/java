package com.github.obase.mysql.stmt;

import java.util.Collection;
import java.util.List;

import com.github.obase.kit.StringKit;
import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;

/**
 * 如果param为null为静态. 动态处理规则, value为null或empty返回false.
 *
 */
@SuppressWarnings("rawtypes")
public class Foreach implements Fragment {

	public static final String DEFAULT_SEPARATOR = " OR ";

	final String psql;
	final String param;
	final String sep;

	public Foreach(String psql, String param, String sep) {
		this.psql = psql;
		this.param = param;
		this.sep = StringKit.isNotEmpty(sep) ? sep : DEFAULT_SEPARATOR;
	}

	@Override
	public boolean isDynamic() {
		return param != null;
	}

	public boolean satisfy(Object obj) {
		return !isEmptyCollect(obj); // 非空集合
	}

	private static boolean isEmptyCollect(Object value) {
		return (value == null) || ((value instanceof Collection) && ((Collection) value).isEmpty());
	}

	@Override
	public void processStatic(StringBuilder psqls, List<String> params) {
		psqls.append(psql);
	}

	@Override
	public Pack satisfy(JdbcMeta meta, Object bean) {
		if (bean == null) {
			return Pack.NO;
		}
		Object value = meta.getValue(bean, param);
		return new Pack(isEmptyCollect(value) ? Pack.CODE_NO : Pack.CODE_YES, value);
	}

	@Override
	public void processDynamic(StringBuilder psqls, List<Param> params, Object value) {
		psqls.append(psql);
		if (value instanceof Collection) {
			int idx = 0;
			for (Object obj : (Collection) value) {
				if (idx != 0) {
					psqls.append(sep);
				}
				psqls.append(psql);
				params.add(new Param(param, idx++, obj));
			}
		} else {
			psqls.append(psql);
			params.add(new Param(param, value));
		}
	}

}
