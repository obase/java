package com.obase.jedis.test;

import java.util.List;

import com.github.obase.jedis.JedisClient;
import com.github.obase.jedis.TransactionCallback;
import com.github.obase.jedis.impl.JedisClientImpl;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		JedisPool pool = new JedisPool("redis://user:f088@172.17.38.204:6379/0");
		JedisClient client = new JedisClientImpl(pool);
		List<Response<?>> rsp = client.execGetResponse(new TransactionCallback() {
			@Override
			public void doInTransaction(Transaction txn, Object... args) {
				txn.get((String) args[0]);
			}
		}, "test");

		System.out.println(rsp);

		System.out.println(client.getex("abcef".getBytes(), 5));
	}

}
