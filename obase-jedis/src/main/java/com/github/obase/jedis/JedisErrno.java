package com.github.obase.jedis;

public interface JedisErrno {

	String SOURCE = "JEDIS";
	int __ = 0x40000;
	int INVALID_SHARD_RATE = __ | 1;
	int DUPLICATE_SHARD_HASH = __ | 2;

}
