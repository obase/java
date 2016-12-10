package com.github.obase.jedis.impl;

import static redis.clients.util.Hashing.MURMUR_HASH;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.obase.MessageException;
import com.github.obase.jedis.JedisClient;
import com.github.obase.jedis.JedisClientSharding;
import com.github.obase.jedis.JedisErrno;
import com.github.obase.jedis.ShardInfo;

public class JedisClientShardingImpl implements JedisClientSharding {

	final List<ShardInfo> shards;
	final TreeMap<Long, JedisClient> clients = new TreeMap<Long, JedisClient>();

	public JedisClientShardingImpl(List<ShardInfo> shards) {
		this.shards = shards;

		if (shards != null) {
			for (ShardInfo info : shards) {
				if (info.getHash() == null) {
					if (info.getRate() < 0 || info.getRate() > 100) {
						throw new MessageException(JedisErrno.SOURCE, JedisErrno.INVALID_SHARD_RATE, "Invalid shard rate: " + info.getRate());
					}
					info.setHash(convRateToLong(info.getRate()));
				}
				if (clients.put(info.getHash(), new JedisClientImpl(info.getJedisPool())) != null) {
					throw new MessageException(JedisErrno.SOURCE, JedisErrno.DUPLICATE_SHARD_HASH, "Duplicate shard hash: " + info.getHash());
				}
			}
		}
	}

	private static long convRateToLong(byte rate) {
		BigDecimal min = BigDecimal.valueOf(Integer.MIN_VALUE);
		return BigDecimal.valueOf(Integer.MAX_VALUE).subtract(min).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(rate)).add(min).longValue();
	}

	@Override
	public JedisClient shard(long key) {
		Map.Entry<Long, JedisClient> entry = clients.floorEntry(key);
		if (entry == null) {
			entry = clients.firstEntry();
		}
		return entry.getValue();
	}

	@Override
	public JedisClient shard(String key) {
		return shard(MURMUR_HASH.hash(key));
	}

	@Override
	public JedisClient shard(byte[] key) {
		return shard(MURMUR_HASH.hash(key));
	}

	@Override
	public Collection<ShardInfo> getAllShardInfo() {
		return shards;
	}

	@Override
	public Collection<JedisClient> getAllShard() {
		return clients.values();
	}

}
