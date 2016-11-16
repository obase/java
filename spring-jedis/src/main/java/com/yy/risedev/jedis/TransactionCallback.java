package com.yy.risedev.jedis;

import redis.clients.jedis.Transaction;

public interface TransactionCallback {

	void doInTransaction(Transaction transaction);
}
