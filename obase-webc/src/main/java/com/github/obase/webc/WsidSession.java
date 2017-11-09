package com.github.obase.webc;

import com.github.obase.security.Principal;

public interface WsidSession {

	/**
	 * 设置key对应的value,并将key的pttl延迟expireMillis
	 */
	void passivate(String key, Principal val, long expireMillis);

	/**
	 * 获取key对应的value,并将key的pttl延迟expireMillis
	 */
	Principal activate(String key, long expireMillis);

}
