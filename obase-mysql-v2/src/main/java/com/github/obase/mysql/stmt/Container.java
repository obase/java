package com.github.obase.mysql.stmt;

import java.util.LinkedList;
import java.util.List;

import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;

/**
 * 创建时已经确定动态或静态, 并做了相邻合并优化.
 *
 */
public abstract class Container implements Fragment {

	public final boolean dynamic;
	public final String psql;
	public final List<String> param;
	public final List<Fragment> fragments; // 动态

	/**
	 * 与Statement的逻辑相同
	 */
	protected Container(List<Fragment> fragments) {
		List<Fragment> result = new LinkedList<Fragment>();
		StringBuilder psqls = new StringBuilder(4096);
		LinkedList<String> params = new LinkedList<String>();
		boolean dynamic = false;
		// 静态合并,动态优化
		for (Fragment f : fragments) {
			if (f.isDynamic()) {
				if (psqls.length() > 0) {
					result.add(new Cdata(psqls.toString(), params));
					psqls.setLength(0);
					params.clear();
				}
				result.add(f);
				dynamic = true;
			} else {
				f.processStatic(psqls, params);
			}
		}
		if (dynamic) {
			// 最后还要判断是否有static遗留
			if (psqls.length() > 0) {
				result.add(new Cdata(psqls.toString(), params));
			}
			this.fragments = result;
			this.psql = null;
			this.param = null;
		} else {
			this.fragments = null;
			this.psql = psqls.toString();
			this.param = params;
		}
		this.dynamic = dynamic;
	}

	@Override
	public final boolean isDynamic() {
		return this.dynamic;
	}

	@Override
	public final Pack satisfy(JdbcMeta meta, Object bean) {

		int si = fragments.size();
		int[] codes = new int[si];
		Object[] values = new Object[si];

		si = 0;
		for (Fragment f : fragments) {
			Pack p = f.satisfy(meta, bean);
			codes[si] = p.code;
			values[si] = p.value;
			si++;
		}

		return new Pack(satisfy(codes), values);
	}

	@Override
	public final void processStatic(StringBuilder psqls, List<String> params) {
		psqls.append(psql);
		params.addAll(param);
	}

	@Override
	public final void processDynamic(StringBuilder psqls, List<Param> params, Object value) {
		Object[] values = (Object[]) value;
		int si = 0;
		for (Fragment f : fragments) {
			f.processDynamic(psqls, params, values[si]);
			si++;
		}
	}

	protected abstract int satisfy(int[] codes);
}
