package com.yy.risedev.jedis;

import redis.clients.jedis.JedisPool;

/**
 * Client shard info, provide every sharding info:
 * 
 * name: sharding name of the client
 * hash: bash hash value for the client, if hash(key) -ge hash, the client would be choose to one of target.
 * jedisPool: proxy target
 * 
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public class ShardInfo {

	// jedis client name
	String name;

	// base hash, if hash(key) >= hash then using the client
	Long hash;

	// base rate[0,100), if hash(key) >= Long.MAX_VALUE
	byte rate;

	// proxy jedisPool;
	JedisPool jedisPool;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}

	public byte getRate() {
		return rate;
	}

	public void setRate(byte rate) {
		this.rate = rate;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

}
