package com.yy.risedev.jedis;

import redis.clients.jedis.Jedis;

public interface JedisCallback<T> {

	T doInJedis(Jedis jedis);

}
