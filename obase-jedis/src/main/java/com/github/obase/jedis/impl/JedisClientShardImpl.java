package com.github.obase.jedis.impl;

import java.util.Map;
import java.util.TreeMap;

import com.github.obase.jedis.JedisClient;
import com.github.obase.jedis.JedisClientShard;
import com.github.obase.jedis.ShardInfo;

public class JedisClientShardImpl implements JedisClientShard {

	final TreeMap<Long, JedisClient> clients = new TreeMap<Long, JedisClient>();

	public JedisClientShardImpl(ShardInfo... infos) {
		for (ShardInfo info : infos) {
			clients.put(info.base, new JedisClientImpl(info.pool));
		}
	}

	public JedisClient shard(long key) {
		Map.Entry<Long, JedisClient> entry = clients.floorEntry(key);
		return entry == null ? null : entry.getValue();
	}
}
