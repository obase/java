package com.github.obase.jedis;

import redis.clients.jedis.Jedis;

public interface JedisCallback<T> {

	T doInJedis(Jedis jedis, Object... args);

}
