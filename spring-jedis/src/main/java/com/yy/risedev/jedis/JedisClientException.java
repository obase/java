package com.yy.risedev.jedis;

/**
 * The Jedis client exception thrown to indicate an execution error
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public class JedisClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JedisClientException() {
		super();
	}

	public JedisClientException(String msg) {
		super(msg);
	}

	public JedisClientException(Throwable t) {
		super(t);
	}

	public JedisClientException(String msg, Throwable t) {
		super(msg, t);
	}
}
