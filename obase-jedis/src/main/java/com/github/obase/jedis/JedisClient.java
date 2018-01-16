package com.github.obase.jedis;

import java.util.List;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScriptingCommands;

public interface JedisClient extends BinaryJedisCommands, MultiKeyBinaryCommands, JedisCommands, MultiKeyCommands, ScriptingCommands {

	List<Object> syncAndReturnAll(PipelineCallback callback, Object... args);

	void sync(PipelineCallback callback, Object... args);

	List<Response<?>> execGetResponse(TransactionCallback callback, Object... args);

	void exec(TransactionCallback callback, Object... args);

	<T> T submit(JedisCallback<T> callback, Object... args);

	JedisPool getJedisPool();

	/**
	 * Please use tryLock(String key, String val, int expireSeconds)
	 */
	@Deprecated
	boolean tryLock(String key, int expireSeconds);

	boolean tryLock(String key, String val, int expireSeconds);

	/**
	 * Please use tryLock(String key, String val, int expireSeconds, long waitMillis, int tryTimes)
	 */
	@Deprecated
	boolean tryLock(String key, int expireSeconds, long waitMillis, int tryTimes);

	boolean tryLock(String key, String val, int expireSeconds, long waitMillis, int tryTimes);

	/**
	 * Please use unlock(String key, String val)
	 */
	@Deprecated
	void unlock(String key);

	int unlock(String key, String val);

	boolean tryHostLock(String key, int expireSeconds);

	byte[] getex(byte[] key, int expirs);

}
