package com.github.obase.mysql;

public interface Part {

	Part[] EMPTY_ARRAY = new Part[0];
	String[] EMPTY_PARAM = new String[0];

	String DEF_SEP = " OR ";

	/**
	 * 判断是否动态标签
	 * 
	 * @return
	 */
	boolean isDynamic();

	String getSeparator();

	/**
	 * 静态标签逻辑
	 * 
	 */
	String getPsql();

	String[] getParams();

	/**
	 * 处理动态逻辑, 并将结果附加到psqls与params
	 */
	boolean processDynamic(JdbcMeta meta, Object bean, StringBuilder psql, ParamBuilder params, int idx);

}
