package com.github.obase.mysql.core;

import java.util.List;

import com.github.obase.mysql.stmt.Pack;
import com.github.obase.mysql.stmt.Param;

/**
 * <pre>
 * 先由isDynamic()判断整个执行环境是动态还是静态的? 动态环境getParam() + satisfy() + processDynamic()来处理拼接, 静态环境调用processStatic()来处理拼接. 
 * 所以整个处理过程分2个阶段, 阶段1由所有Fragment的isDynamic()确定是环境, 阶段2调用环境处理方法获取psql与params
 * 一般地在创建Container对象时已经完成阶段1, 并针缓存静态环境的结果.
 * </pre>
 */
public interface Fragment {

	/**
	 * 判断是否动态标签
	 * 
	 * @return
	 */
	boolean isDynamic();

	/**
	 * 是否满足动态条件
	 * 
	 * @return TRUE表示动态满足, FALSE表示动态不满足. 只有isDynamic()为true的元素才参与决择.
	 */
	Pack satisfy(JdbcMeta meta, Object bean);

	/**
	 * 处理静态逻辑
	 */
	void processStatic(StringBuilder psqls, List<String> params);

	/**
	 * 处理动态逻辑
	 */
	void processDynamic(StringBuilder psqls, List<Param> params, Object value);
}
