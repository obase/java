package com.obase.jedis.test;

import java.util.concurrent.TimeUnit;

import com.github.obase.jedis.JedisClient;
import com.github.obase.jedis.impl.JedisClientImpl;

import redis.clients.jedis.JedisPool;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		JedisPool jedisPool = new JedisPool("localhost", 6379);
		JedisClient client = new JedisClientImpl(jedisPool);

		System.out.println("lock");
		client.tryLock("LOCK", "abc", 123);
		TimeUnit.SECONDS.sleep(40);
		System.out.println("unlock 123");
		client.unlock("LOCK", "123");
		TimeUnit.SECONDS.sleep(40);
		System.out.println("unlock abc");
		client.unlock("LOCK", "abc");
	}

}
