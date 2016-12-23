package com.github.obase.jedis;

import java.util.List;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.Response;

public interface JedisClient extends BinaryJedisCommands, MultiKeyBinaryCommands, JedisCommands, MultiKeyCommands {

	List<Object> syncAndReturnAll(PipelineCallback callback, Object... args);

	void sync(PipelineCallback callback, Object... args);

	List<Response<?>> execGetResponse(TransactionCallback callback, Object... args);

	void exec(TransactionCallback callback, Object... args);

	<T> T submit(JedisCallback<T> callback, Object... args);

	JedisPool getJedisPool();

	boolean tryLock(String key, int expireSeconds);

	boolean tryLock(String key, int expireSeconds, long waitMillis, int tryTimes);

	void unlock(String key);

	byte[] getex(byte[] key, int expirs);

}
