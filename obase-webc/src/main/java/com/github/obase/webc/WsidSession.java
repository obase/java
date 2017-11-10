package com.github.obase.webc;

public interface WsidSession {

	/**
	 * 设置key对应的value,并将key的pttl延迟expireMillis. expireMillis为负表示永久保存, 为0表示立即删除
	 */
	void passivate(String key, String val, long expireMillis);

	/**
	 * 获取key对应的value,并将key的pttl延迟expireMillis. expireMillis为负表示永久保存, 为0表示立即删除
	 */
	String activate(String key, long expireMillis);

}
