package com.github.obase.security;

import java.io.Serializable;

/**
 * marked interface for WsidServletMethodProcessor
 */
public interface Principal extends Serializable {

	/**
	 * 获取标识
	 */
	String getKey();

}
