package com.github.obase.mysql.stmt;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.obase.MessageException;
import com.github.obase.kit.StringKit;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;

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
public class Generic implements Fragment {

	protected final String psql;
	protected final Param[] params;
	protected final Fragment[] children; // 动态
	protected final String s; // 指定多值拼接符, 默认为" OR "

	protected final String p; // 首个param的名称
	protected final boolean d; // 动态标签也会退化为静态标签: 没有子元素也没有参数

	public Generic(boolean dynamic, String s, List<Fragment> children) {

		List<Fragment> _children = new LinkedList<Fragment>();
		StringBuilder _psql = new StringBuilder(2048);
		LinkedList<Param> _params = new LinkedList<Param>();
		// 合并优化静态,如果无动态则psqls与params存的是静态
		for (Fragment f : children) {
			if (f.isDynamic()) {
				if (_psql.length() > 0) {
					_children.add(new Static(_psql.toString(), _params));
					_psql.setLength(0);
					_params.clear();
				}
				_children.add(f);
			} else {
				String _s = f.getPsql();
				if (StringKit.isNotBlank(_s)) {
					_psql.append(_s);
					Collections.addAll(_params, f.getParams());
				}
			}
		}
		// 包含动态子标签
		if (_children.size() > 0 && _psql.length() > 0) {
			_children.add(new Static(_psql.toString(), _params));
			_psql.setLength(0);
			_params.clear();
		}

		this.psql = _psql.toString();
		this.params = _params.isEmpty() ? Param.EMPTY_ARRAY : _params.toArray(new Param[_params.size()]);
		this.children = _children.isEmpty() ? Fragment.EMPTY_ARRAY : _children.toArray(new Fragment[_children.size()]);
		this.s = StringKit.isEmpty(s) ? DEF_SEP : s;

		this.p = this.params.length > 0 ? this.params[0].name : null; // 如果包含子标签就不会有params
		this.d = this.children.length > 0 || (dynamic && this.params.length > 0); // 标签本身为动态且有参数
		// 限制动态标签只允许包含子标签或者至多一个参数
		if (this.d && this.params.length > 1) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_EXCEED_PARAMS, "Dynamic fragment contains more than 1 params");
		}

	}

	@Override
	public final boolean isDynamic() {
		return this.d;
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
	public final boolean processDynamic(JdbcMeta meta, Object bean, StringBuilder psqls, List<Param> params, int idx) {

		if (p != null) { // 不包含子标签

			Object v = meta.getValue(bean, p);

			if (v instanceof Collection) {
				Collection c = (Collection) v;
				if (c.size() > 0) {
					psqls.append(prefix(idx));
					// 迭代集合
					int i = 0;
					for (Object o : c) {
						if (i != 0) {
							psqls.append(s);
						}
						psqls.append(this.psql);
						params.add(new Param(p, i, o));
						i++;
					}
					psqls.append(suffix());

					return true;
				}
			} else if (v != null) {
				psqls.append(prefix(idx));
				psqls.append(this.psql);
				params.add(new Param(p, v));
				psqls.append(suffix());

				return true;
			}

		} else { // 包含子标签

			StringBuilder _psql = new StringBuilder(2048);
			List<Param> _params = new LinkedList<Param>();

			boolean cret = !d; // 如果静态直接
			StringBuilder sb = new StringBuilder(1024);
			List<Param> ps = new LinkedList<Param>();
			for (int i = 0; i < children.length; i++) {
				Fragment f = children[i];
				if (f.isDynamic()) {
					sb.setLength(0);
					ps.clear();
					if (f.processDynamic(meta, bean, sb, ps, i)) {
						_psql.append(sb);
						_params.addAll(ps);
						cret = true;
					}
				} else {
					_psql.append(f.getPsql());
					Collections.addAll(_params, f.getParams());
				}
			}

			// 某个动态子标签为true才添加到最终结果
			if (cret) {
				psqls.append(prefix(idx));
				psqls.append(_psql);
				params.addAll(_params);
				psqls.append(suffix());

				return true;
			}
		}

		return false;
	}

	protected String prefix(int idx) {
		return "";
	}

	protected String suffix() {
		return "";
	}

}
