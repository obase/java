package com.yy.risedev.jedis;

import java.util.List;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.Response;

public interface JedisClient extends BinaryJedisCommands, MultiKeyBinaryCommands, JedisCommands, MultiKeyCommands {

	List<Object> syncAndReturnAll(PipelineCallback callback);

	void sync(PipelineCallback callback);

	List<Response<?>> execGetResponse(TransactionCallback callback);

	void exec(TransactionCallback callback);

	<T> T submit(JedisCallback<T> callback);

	JedisPool getJedisPool();

	boolean tryLock(String key, int expireSeconds);

	boolean tryLock(String key, int expireSeconds, long waitMillis, int tryTimes);

	void unlock(String key);

	byte[] getex(byte[] key, int expirs);

}
