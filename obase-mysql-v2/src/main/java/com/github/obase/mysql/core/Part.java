package com.github.obase.mysql.core;

import com.github.obase.mysql.stmt.Param;

public interface Part {

	Part[] EMPTY_ARRAY = new Part[0];

	String DEF_SEP = " OR ";

	/**
	 * 判断是否动态标签
	 * 
	 * @return
	 */
	boolean isDynamic();

	/**
	 * 静态标签逻辑
	 * 
	 */
	String getPsql();

	Param[] getParams();

	/**
	 * 处理动态逻辑, 并将结果附加到psqls与params
	 */
	boolean processDynamic(JdbcMeta meta, Object bean, DLink<String> psqls, DLink<Param> params, int idx);

}
