package com.github.obase.security;

import java.io.Serializable;

/**
 * marked interface for WsidServletMethodProcessor
 */
public interface Principal extends Serializable {

	/**
	 * 自身序列化
	 */
	String encode();

	/**
	 * 自身反序列化
	 */
	Principal decode(String value);

}
