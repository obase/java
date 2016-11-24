package com.github.obase.config;

/**
 * The configure exception thrown to indicate an execution error
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String msg) {
		super(msg);
	}

	public ConfigurationException(Throwable t) {
		super(t);
	}

	public ConfigurationException(String msg, Throwable t) {
		super(msg, t);
	}
}
