package com.github.obase.jedis;

import redis.clients.jedis.Transaction;

public interface TransactionCallback {

	void doInTransaction(Transaction transaction, Object... args);
}
