package com.github.obase.jedis.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.github.obase.jedis.JedisCallback;
import com.github.obase.jedis.JedisClient;
import com.github.obase.jedis.PipelineCallback;
import com.github.obase.jedis.TransactionCallback;
import com.github.obase.kit.HostKit;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

public class JedisClientImpl implements JedisClient {

	final JedisPool jedisPool;

	public JedisClientImpl(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public String set(byte[] key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String set(byte[] key, byte[] value, byte[] nxxx) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key, value, nxxx);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key, value, nxxx, expx, time);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] get(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean exists(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long persist(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.persist(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String type(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.type(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long expire(byte[] key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expire(key, seconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pexpire(key, milliseconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pexpire(byte[] key, long milliseconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pexpire(key, milliseconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long expireAt(byte[] key, long unixTime) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expireAt(key, unixTime);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pexpireAt(byte[] key, long millisecondsTimestamp) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pexpireAt(key, millisecondsTimestamp);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long ttl(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ttl(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean setbit(byte[] key, long offset, boolean value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setbit(key, offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean setbit(byte[] key, long offset, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setbit(key, offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean getbit(byte[] key, long offset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getbit(key, offset);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long setrange(byte[] key, long offset, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setrange(key, offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] getrange(byte[] key, long startOffset, long endOffset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getrange(key, startOffset, endOffset);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] getSet(byte[] key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getSet(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long setnx(byte[] key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setnx(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String setex(byte[] key, int seconds, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setex(key, seconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long decrBy(byte[] key, long integer) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decrBy(key, integer);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long decr(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long incrBy(byte[] key, long integer) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrBy(key, integer);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double incrByFloat(byte[] key, double value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrByFloat(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long incr(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long append(byte[] key, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.append(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] substr(byte[] key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.substr(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hset(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hset(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] hget(byte[] key, byte[] field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hsetnx(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String hmset(byte[] key, Map<byte[], byte[]> hash) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmset(key, hash);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmget(key, fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hincrBy(byte[] key, byte[] field, long value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrBy(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double hincrByFloat(byte[] key, byte[] field, double value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrByFloat(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean hexists(byte[] key, byte[] field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hexists(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hdel(byte[] key, byte[]... field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hdel(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hlen(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hlen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> hkeys(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hkeys(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Collection<byte[]> hvals(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hvals(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hgetAll(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long rpush(byte[] key, byte[]... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpush(key, args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lpush(byte[] key, byte[]... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpush(key, args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long llen(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.llen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> lrange(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String ltrim(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ltrim(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] lindex(byte[] key, long index) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lindex(key, index);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lset(byte[] key, long index, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lset(key, index, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lrem(byte[] key, long count, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrem(key, count, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] lpop(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] rpop(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sadd(byte[] key, byte[]... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sadd(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> smembers(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smembers(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long srem(byte[] key, byte[]... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srem(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] spop(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.spop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> spop(byte[] key, long count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.spop(key, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long scard(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scard(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean sismember(byte[] key, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] srandmember(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> srandmember(byte[] key, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long strlen(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.strlen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(byte[] key, double score, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(byte[] key, double score, byte[] member, ZAddParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, scoreMembers);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, scoreMembers, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrange(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrem(byte[] key, byte[]... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrem(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zincrby(byte[] key, double score, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zincrby(key, score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zincrby(byte[] key, double score, byte[] member, ZIncrByParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zincrby(key, score, member, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrank(byte[] key, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrank(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrevrank(byte[] key, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrank(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrange(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeWithScores(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeWithScores(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcard(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcard(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zscore(byte[] key, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> sort(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key, sortingParameters);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcount(byte[] key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcount(byte[] key, byte[] min, byte[] max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByRank(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByRank(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByScore(byte[] key, double start, double end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByScore(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByScore(byte[] key, byte[] start, byte[] end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByScore(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zlexcount(byte[] key, byte[] min, byte[] max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zlexcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByLex(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByLex(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByLex(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByLex(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByLex(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.linsert(key, where, pivot, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lpushx(byte[] key, byte[]... arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpushx(key, arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long rpushx(byte[] key, byte[]... arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpush(key, arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Deprecated
	@Override
	public List<byte[]> blpop(byte[] arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Deprecated
	@Override
	public List<byte[]> brpop(byte[] arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long del(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] echo(byte[] arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.echo(arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long move(byte[] key, int dbIndex) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.move(key, dbIndex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitcount(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitcount(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitcount(byte[] key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitcount(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pfadd(byte[] key, byte[]... elements) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfadd(key, elements);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public long pfcount(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfcount(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geoadd(key, longitude, latitude, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long geoadd(byte[] key, Map<byte[], GeoCoordinate> memberCoordinateMap) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geoadd(key, memberCoordinateMap);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geodist(key, member1, member2);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geodist(key, member1, member2, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> geohash(byte[] key, byte[]... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geohash(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoCoordinate> geopos(byte[] key, byte[]... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geopos(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadius(key, longitude, latitude, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadius(key, longitude, latitude, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMember(key, member, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMember(key, member, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key, cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<byte[]> sscan(byte[] key, byte[] cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<byte[]> sscan(byte[] key, byte[] cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key, cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Tuple> zscan(byte[] key, byte[] cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Tuple> zscan(byte[] key, byte[] cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key, cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long del(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long exists(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> blpop(int timeout, byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(timeout, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> brpop(int timeout, byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> blpop(byte[]... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> brpop(byte[]... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> keys(byte[] pattern) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.keys(pattern);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> mget(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mget(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String mset(byte[]... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mset(keysvalues);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long msetnx(byte[]... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.msetnx(keysvalues);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rename(byte[] oldkey, byte[] newkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rename(oldkey, newkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long renamenx(byte[] oldkey, byte[] newkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.renamenx(oldkey, newkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpoplpush(srckey, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> sdiff(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sdiff(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sdiffstore(byte[] dstkey, byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sdiffstore(dstkey, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> sinter(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sinter(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sinterstore(byte[] dstkey, byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sinterstore(dstkey, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long smove(byte[] srckey, byte[] dstkey, byte[] member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smove(srckey, dstkey, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sort(byte[] key, SortingParams sortingParameters, byte[] dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key, sortingParameters, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sort(byte[] key, byte[] dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<byte[]> sunion(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sunion(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sunionstore(byte[] dstkey, byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sunionstore(dstkey, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String watch(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.watch(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String unwatch() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.unwatch();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zinterstore(byte[] dstkey, byte[]... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zinterstore(dstkey, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zinterstore(byte[] dstkey, ZParams params, byte[]... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zinterstore(dstkey, params, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zunionstore(byte[] dstkey, byte[]... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zunionstore(dstkey, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zunionstore(byte[] dstkey, ZParams params, byte[]... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zunionstore(dstkey, params, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpoplpush(source, destination, timeout);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long publish(byte[] channel, byte[] message) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.publish(channel, message);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.subscribe(jedisPubSub, channels);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.psubscribe(jedisPubSub, patterns);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] randomBinaryKey() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.randomBinaryKey();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitop(BitOP op, byte[] destKey, byte[]... srcKeys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitop(op, destKey, srcKeys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String pfmerge(byte[] destkey, byte[]... sourcekeys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfmerge(destkey, sourcekeys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pfcount(byte[]... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfcount(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key, value, nxxx, expx, time);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String set(String key, String value, String nxxx) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key, value, nxxx);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long persist(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.persist(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String type(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.type(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expire(key, seconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expireAt(key, unixTime);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pexpireAt(key, millisecondsTimestamp);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ttl(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pttl(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pttl(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setbit(key, offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setbit(key, offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean getbit(String key, long offset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getbit(key, offset);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setrange(key, offset, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getrange(key, startOffset, endOffset);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String getSet(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getSet(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long setnx(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setnx(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setex(key, seconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.psetex(key, milliseconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long decrBy(String key, long integer) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decrBy(key, integer);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long decr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long incrBy(String key, long integer) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrBy(key, integer);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double incrByFloat(String key, double value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrByFloat(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long append(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.append(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String substr(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.substr(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hset(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hsetnx(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmset(key, hash);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmget(key, fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrBy(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double hincrByFloat(String key, String field, double value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrByFloat(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean hexists(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hexists(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hdel(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hdel(key, fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long hlen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hlen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> hkeys(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hkeys(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> hvals(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hvals(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hgetAll(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long rpush(String key, String... strings) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpush(key, strings);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lpush(String key, String... strings) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpush(key, strings);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.llen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String ltrim(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ltrim(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lindex(String key, long index) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lindex(key, index);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lset(String key, long index, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lset(key, index, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lrem(String key, long count, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrem(key, count, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sadd(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sadd(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smembers(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long srem(String key, String... member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srem(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String spop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.spop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> spop(String key, long count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.spop(key, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scard(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Boolean sismember(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String srandmember(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> srandmember(String key, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long strlen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.strlen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, scoreMembers);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, scoreMembers, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrem(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrem(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zincrby(key, score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zincrby(String key, double score, String member, ZIncrByParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zincrby(key, score, member, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrank(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrank(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zrevrank(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrank(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeWithScores(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeWithScores(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcard(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double zscore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> sort(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key, sortingParameters);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcount(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zcount(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByRank(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByScore(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByScore(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zlexcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByLex(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrangeByLex(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByLex(key, max, min);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeByLex(key, max, min, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zremrangeByLex(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.linsert(key, where, pivot, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long lpushx(String key, String... strings) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpush(key, strings);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long rpushx(String key, String... strings) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpush(key, strings);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> blpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(timeout, key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long del(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String echo(String string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.echo(string);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long move(String key, int dbIndex) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.move(key, dbIndex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitcount(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitcount(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitcount(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitpos(String key, boolean value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitpos(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitpos(String key, boolean value, BitPosParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitpos(key, value, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key, cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key, cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key, cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long pfadd(String key, String... elements) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfadd(key, elements);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public long pfcount(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfcount(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long geoadd(String key, double longitude, double latitude, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geoadd(key, longitude, latitude, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geoadd(key, memberCoordinateMap);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double geodist(String key, String member1, String member2) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geodist(key, member1, member2);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Double geodist(String key, String member1, String member2, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geodist(key, member1, member2, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> geohash(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geohash(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoCoordinate> geopos(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.geopos(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadius(key, longitude, latitude, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadius(key, longitude, latitude, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMember(key, member, radius, unit);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.georadiusByMember(key, member, radius, unit, param);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long del(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long exists(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(timeout, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> blpop(String... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> brpop(String... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> keys(String pattern) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.keys(pattern);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> mget(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mget(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String mset(String... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mset(keysvalues);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long msetnx(String... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.msetnx(keysvalues);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rename(String oldkey, String newkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rename(oldkey, newkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.renamenx(oldkey, newkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpoplpush(srckey, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> sdiff(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sdiff(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sdiffstore(dstkey, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> sinter(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sinter(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sinterstore(dstkey, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smove(srckey, dstkey, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key, sortingParameters, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sort(String key, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sort(key, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> sunion(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sunion(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sunionstore(dstkey, keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String watch(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.watch(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zinterstore(String dstkey, String... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zinterstore(dstkey, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zinterstore(dstkey, params, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zunionstore(String dstkey, String... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zunionstore(dstkey, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zunionstore(dstkey, params, sets);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpoplpush(source, destination, timeout);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long publish(String channel, String message) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.publish(channel, message);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.subscribe(jedisPubSub, channels);

		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.psubscribe(jedisPubSub, patterns);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String randomKey() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.randomKey();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitop(op, destKey, srcKeys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	@Deprecated
	public ScanResult<String> scan(int cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scan(cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> scan(String cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scan(cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public ScanResult<String> scan(String cursor, ScanParams params) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scan(cursor, params);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfmerge(destkey, sourcekeys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public long pfcount(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.pfcount(keys);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public <T> T submit(JedisCallback<T> callback, Object... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return callback.doInJedis(jedis, args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<Response<?>> execGetResponse(TransactionCallback callback, Object... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Transaction tx = jedis.multi();
			callback.doInTransaction(tx, args);
			return tx.execGetResponse();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void exec(TransactionCallback callback, Object... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Transaction tx = jedis.multi();
			callback.doInTransaction(tx, args);
			tx.exec();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<Object> syncAndReturnAll(PipelineCallback callback, Object... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline = jedis.pipelined();
			callback.doInPipeline(pipeline, args);
			return pipeline.syncAndReturnAll();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void sync(PipelineCallback callback, Object... args) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline = jedis.pipelined();
			callback.doInPipeline(pipeline, args);
			pipeline.sync();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	@Deprecated
	public List<String> blpop(String arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.blpop(arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	@Deprecated
	public List<String> brpop(String arg) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(arg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	@Deprecated
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	@Deprecated
	public ScanResult<String> sscan(String key, int cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	@Deprecated
	public ScanResult<Tuple> zscan(String key, int cursor) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscan(key, cursor);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	@Override
	public boolean tryLock(String key, int expireSeconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Long ret = jedis.incr(key);
			if (ret == 1) {
				jedis.expire(key, expireSeconds);
				return true;
			}
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean tryLock(String key, int expireSeconds, long waitMillis, int tryTimes) {
		try {
			if (tryLock(key, expireSeconds)) {
				return true;
			} else {
				for (int i = 0; i < tryTimes; i++) {
					TimeUnit.MILLISECONDS.sleep(waitMillis);
					if (tryLock(key, expireSeconds)) {
						return true;
					}
				}
				return false;
			}
		} catch (InterruptedException e) {
			// if occur exception, treated as false
			return false;
		}
	}

	@Override
	public void unlock(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/*
	 * @Override FOR 2.9.0 public List<byte[]> bitfield(byte[] key, byte[]... arguments) { Jedis jedis = null; try { jedis = jedisPool.getResource(); return jedis.bitfield(key, arguments); } finally { if (jedis != null) { jedis.close(); } } }
	 */

	/*
	 * @Override FOR: 2.9.0 public List<Long> bitfield(String key, String... arguments) { Jedis jedis = null; try { jedis = jedisPool.getResource(); return jedis.bitfield(key, arguments); } finally { if (jedis != null) { jedis.close(); } } }
	 */

	@Override
	public byte[] getex(byte[] key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Transaction tx = jedis.multi();
			Response<byte[]> data = tx.get(key);
			tx.expire(key, seconds);
			tx.exec();
			return data.get();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean tryHostLock(String key, int expireSeconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String val = jedis.get(key);
			if (val == null) {
				jedis.setex(key, expireSeconds, HostKit.HOSTID);
				return true;
			} else if (HostKit.HOSTID.equals(val)) {
				jedis.expire(key, expireSeconds);
				return true;
			} else {
				return false;
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<byte[]> bitfield(byte[] key, byte[]... arguments) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitfield(key, arguments);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public List<Long> bitfield(String key, String... arguments) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.bitfield(key, arguments);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean tryLock(String key, String val, int expireSeconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis.setnx(key, val) > 0) {
				jedis.expire(key, expireSeconds);
				return true;
			}
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean tryLock(String key, String val, int expireSeconds, long waitMillis, int tryTimes) {
		try {
			if (tryLock(key, val, expireSeconds)) {
				return true;
			} else {
				for (int i = 0; i < tryTimes; i++) {
					TimeUnit.MILLISECONDS.sleep(waitMillis);
					if (tryLock(key, val, expireSeconds)) {
						return true;
					}
				}
				return false;
			}
		} catch (InterruptedException e) {
			// if occur exception, treated as false
			return false;
		}
	}

	@Override
	public int unlock(String key, String val) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Number ret = (Number) jedis.eval("if redis.call('get',KEYS[1])==KEYS[2] then return redis.call('del',KEYS[1]) else return 0 end", 2, key, val);
			return ret == null ? 0 : ret.intValue();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

}
