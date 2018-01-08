package com.github.obase.jedis;

import redis.clients.jedis.JedisPool;

public class ShardInfo {

	public final JedisPool pool;
	public final long base;

	public ShardInfo(JedisPool pool, long base) {
		this.pool = pool;
		this.base = base;
	}

}
