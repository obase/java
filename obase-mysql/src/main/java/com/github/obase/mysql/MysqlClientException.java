package com.github.obase.mysql;

/**
 * The MySQL client exception thrown to indicate an execution error
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public class MysqlClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MysqlClientException() {
		super();
	}

	public MysqlClientException(String msg) {
		super(msg);
	}

	public MysqlClientException(Throwable t) {
		super(t);
	}

	public MysqlClientException(String msg, Throwable t) {
		super(msg, t);
	}
}
