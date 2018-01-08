package com.github.obase.jedis;

/**
 * The shard jedis client bash hash value.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public interface JedisClientShard {

	JedisClient shard(long key);

}
