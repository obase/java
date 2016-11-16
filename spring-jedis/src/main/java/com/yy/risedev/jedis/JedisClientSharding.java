package com.yy.risedev.jedis;

import java.util.Collection;

/**
 * The shard jedis client bash hash value.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public interface JedisClientSharding {

	JedisClient shard(long key);

	JedisClient shard(String key);

	JedisClient shard(byte[] key);

	Collection<ShardInfo> getAllShardInfo();

	Collection<JedisClient> getAllShard();

}
