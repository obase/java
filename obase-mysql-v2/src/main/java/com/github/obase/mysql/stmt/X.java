package com.github.obase.mysql.stmt;

import java.util.Collection;

import com.github.obase.kit.StringKit;
import com.github.obase.mysql.core.DLink;
import com.github.obase.mysql.core.DNode;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.Part;

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
	protected Param[] params;
	// 包含子元素
	protected Part[] parts; // 动态

	public final X reset(String s, String psql, Param param) {
		this.s = StringKit.isEmpty(s) ? DEF_SEP : s;
		this.p = param.name;

		this.psql = psql;
		this.params = new Param[] { param };
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
	public final String getPsql() {
		return this.psql;
	}

	@Override
	public final Param[] getParams() {
		return this.params;
	}

	// 该方法执行前提: dynamic=true
	@SuppressWarnings("rawtypes")
	public final boolean processDynamic(JdbcMeta meta, Object bean, DLink<String> psqls, DLink<Param> params, int idx) {

		if (p != null) { // 不包含子标签

			Object v = meta.getValue(bean, p);

			if (v instanceof Collection) {
				Collection c = (Collection) v;
				if (c.size() > 0) {
					psqls.tail(prefix(idx));
					// 迭代集合
					int i = 0;
					for (Object o : c) {
						if (i != 0) {
							psqls.tail(s);
						}
						psqls.tail(this.psql);
						params.tail(new Param(p, i, o));
						i++;
					}
					psqls.tail(suffix());

					return true;
				}
			} else if (v != null) {
				psqls.tail(prefix(idx));
				psqls.tail(this.psql);
				params.tail(new Param(p, v));
				psqls.tail(suffix());

				return true;
			}

			return false;
		} else { // 包含子标签

			DNode<String> tpsqls = psqls.tail; // 标记尾用于回滚
			DNode<Param> tparams = params.tail; // 标记尾用于回滚

			boolean cret = false;
			int sidx = 0;
			for (Part f : parts) {
				if (f.isDynamic()) {
					if (f.processDynamic(meta, bean, psqls, params, sidx++)) {
						cret = true;
					}
				} else {
					psqls.tail(f.getPsql());
					params.tail(f.getParams());
				}
			}

			// 某个动态子标签为true才添加到最终结果
			if (!cret) {
				psqls.chop(tpsqls);
				params.chop(tparams);
				return false;
			}
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
