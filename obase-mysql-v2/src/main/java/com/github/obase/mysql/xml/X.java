package com.github.obase.mysql.xml;

import java.util.Collection;

import com.github.obase.kit.StringKit;
import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.impl.ParamBuilder;

/**
 * 标签只有2种情况:
 * 
 * 1. 包含子元素
 * 
 * 2. 不包含子元素.
 * 
 * 每个标签只能包含一个参数用于判断.
 *
 */
public class X implements Part {

	protected String s; // 指定多值拼接符, 默认为" OR "
	// 由此判断是否包含子元素
	public String p;

	// 不包含子元素
	protected String psql;
	protected String[] params;
	// 包含子元素
	protected Part[] parts; // 动态

	public final X reset(String s, String psql, String param) {
		this.s = StringKit.isEmpty(s) ? DEF_SEP : s;
		this.p = param;

		this.psql = psql;
		this.params = new String[] { param };
		this.parts = null;

		return this;
	}

	public final X reset(String s, Part[] parts) {
		this.s = StringKit.isEmpty(s) ? DEF_SEP : s;
		this.p = null;
		this.parts = parts;
		this.psql = null;
		this.params = null;

		return this;
	}

	@Override
	public final boolean isDynamic() {
		return true;
	}

	@Override
	public String getSeparator() {
		return this.s;
	}

	@Override
	public final String getPsql() {
		return this.psql;
	}

	@Override
	public final String[] getParams() {
		return this.params;
	}

	// 该方法执行前提: dynamic=true
	@SuppressWarnings("rawtypes")
	public final boolean processDynamic(JdbcMeta meta, Object bean, StringBuilder psqls, ParamBuilder params, int idx) {

		if (bean == null) {
			return false; // FIXBUG: 如果参数为null,直接返回false
		}

		if (p != null) { // 不包含子标签

			Object v = meta.getValue(bean, p);

			if (v instanceof Collection) {
				Collection c = (Collection) v;
				if (c.size() > 0) {
					psqls.append(prefix(idx));
					// 迭代集合
					int set = 0; // 注意:集合下标从1开始
					for (Object o : c) {
						if (set != 0) {
							psqls.append(s);
						}
						psqls.append(this.psql);
						params.append(p, ++set, o); // 注意:集合下标从1开始
					}
					psqls.append(suffix());

					return true;
				}
			} else if (v != null) {
				psqls.append(prefix(idx));
				psqls.append(this.psql);
				params.append(p, v);
				psqls.append(suffix());

				return true;
			}

			return false;
		} else { // 包含子标签

			int tlength = psqls.length(); // 标记尾用于回滚
			int plength = params.length(); // 标记尾用于回滚

			psqls.append(prefix(idx));
			boolean cret = false;
			int i = 0;
			for (Part f : parts) {
				if (f.isDynamic()) {
					if (f.processDynamic(meta, bean, psqls, params, i)) {
						cret = true;
						i++; // FIXBUG: 只有成功执行才把下标加1
					}
				} else {
					psqls.append(f.getPsql());
					params.append(f.getParams());
					i++; // FIXBUG: 只有成功执行才把下标加1
				}
			}

			// 某个动态子标签为true才添加到最终结果
			if (!cret) {
				psqls.setLength(tlength);
				params.setLength(plength);
				return false;
			}
			psqls.append(suffix());
			return true;
		}

	}

	protected String prefix(int idx) {
		return "";
	}

	protected String suffix() {
		return "";
	}

}
