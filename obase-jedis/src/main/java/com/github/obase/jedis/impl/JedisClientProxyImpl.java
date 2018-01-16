package com.github.obase.jedis.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.obase.jedis.JedisCallback;
import com.github.obase.jedis.JedisClient;
import com.github.obase.jedis.PipelineCallback;
import com.github.obase.jedis.TransactionCallback;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

public class JedisClientProxyImpl implements JedisClient {

	static Logger logger = LoggerFactory.getLogger(JedisClientProxyImpl.class);

	final ExecutorService excutor;
	final JedisClient master;
	final JedisClient[] slaves;
	final boolean ignoreError;

	public JedisClientProxyImpl(JedisPool[] pools, ExecutorService excutor, boolean ignoreError) {
		this.master = new JedisClientImpl(pools[0]);
		this.slaves = new JedisClient[pools.length - 1];
		for (int i = 1; i < pools.length; i++) {
			this.slaves[i - 1] = new JedisClientImpl(pools[i]);
		}
		this.excutor = excutor;
		this.ignoreError = ignoreError;
	}

	public JedisClientProxyImpl(JedisPool[] pools, int maxThreads) {
		this(pools, new ThreadPoolExecutor(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()), true);
	}

	@Override
	public String set(byte[] key, byte[] value) {
		String ret = master.set(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.set(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String set(byte[] key, byte[] value, byte[] nxxx) {
		String ret = master.set(key, value, nxxx);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.set(key, value, nxxx);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		String ret = master.set(key, value, nxxx, expx, time);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.set(key, value, nxxx, expx, time);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] get(byte[] key) {
		byte[] ret;
		if ((ret = master.get(key)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.get(key)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Boolean exists(byte[] key) {
		if (master.exists(key)) {
			return true;
		}
		for (JedisClient s : slaves) {
			try {
				if (s.exists(key)) {
					return true;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public Long persist(byte[] key) {
		Long ret = master.persist(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.persist(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	static final String NONE = "none";

	@Override
	public String type(byte[] key) {
		String ret;
		if (!NONE.equals(ret = master.type(key))) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if (!NONE.equals(ret = s.type(key))) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return NONE;
	}

	@Override
	public Long expire(byte[] key, int seconds) {
		Long ret = master.expire(key, seconds);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.expire(key, seconds);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	@Deprecated
	public Long pexpire(String key, long milliseconds) {
		Long ret = master.pexpire(key, milliseconds);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.pexpire(key, milliseconds);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long pexpire(byte[] key, long milliseconds) {
		Long ret = master.pexpire(key, milliseconds);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.pexpire(key, milliseconds);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long expireAt(byte[] key, long unixTime) {
		Long ret = master.expireAt(key, unixTime);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.expireAt(key, unixTime);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long pexpireAt(byte[] key, long millisecondsTimestamp) {
		Long ret = master.pexpireAt(key, millisecondsTimestamp);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.pexpireAt(key, millisecondsTimestamp);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	static long LNIL = -1L;

	@Override
	public Long ttl(byte[] key) {
		Long ret;
		if ((ret = master.ttl(key)) != LNIL) {
			return ret;
		} else if (master.exists(key)) {
			return LNIL;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.ttl(key)) != LNIL) {
					return ret;
				} else if (s.exists(key)) {
					return LNIL;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return LNIL;
	}

	@Override
	public Boolean setbit(byte[] key, long offset, boolean value) {
		Boolean ret = master.setbit(key, offset, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setbit(key, offset, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Boolean setbit(byte[] key, long offset, byte[] value) {
		Boolean ret = master.setbit(key, offset, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setbit(key, offset, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Boolean getbit(byte[] key, long offset) {
		if (master.getbit(key, offset)) {
			return true;
		} else if (master.exists(key)) {
			return false;
		}
		for (JedisClient s : slaves) {
			try {
				if (s.getbit(key, offset)) {
					return true;
				} else if (s.exists(key)) {
					return false;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public Long setrange(byte[] key, long offset, byte[] value) {
		Long ret = master.setrange(key, offset, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setrange(key, offset, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] getrange(byte[] key, long startOffset, long endOffset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getSet(byte[] key, byte[] value) {
		byte[] ret = master.getSet(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.getSet(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long setnx(byte[] key, byte[] value) {
		Long ret = master.setnx(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setnx(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String setex(byte[] key, int seconds, byte[] value) {
		String ret = master.setex(key, seconds, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setex(key, seconds, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long decrBy(byte[] key, long integer) {
		Long ret = master.decrBy(key, integer);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.decrBy(key, integer);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long decr(byte[] key) {
		Long ret = master.decr(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.decr(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long incrBy(byte[] key, long integer) {
		Long ret = master.incrBy(key, integer);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.incrBy(key, integer);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double incrByFloat(byte[] key, double value) {
		Double ret = master.incrByFloat(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.incrByFloat(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long incr(byte[] key) {
		Long ret = master.incr(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.incr(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long append(byte[] key, byte[] value) {
		Long ret = master.append(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.append(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	static final byte[] BNIL = new byte[0];

	@Override
	public byte[] substr(byte[] key, int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long hset(byte[] key, byte[] field, byte[] value) {
		Long ret = master.hset(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hset(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] hget(byte[] key, byte[] field) {
		byte[] ret;
		if ((ret = master.hget(key, field)) != null) {
			return ret;
		} else if (master.exists(key)) {
			return null;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hget(key, field)) != null) {
					return ret;
				} else if (s.exists(key)) {
					return null;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		Long ret = master.hsetnx(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hsetnx(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String hmset(byte[] key, Map<byte[], byte[]> hash) {
		String ret = master.hmset(key, hash);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hmset(key, hash);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		if (master.exists(key)) {
			return master.hmget(key, fields);
		}
		for (JedisClient s : slaves) {
			try {
				if (s.exists(key)) {
					return s.hmget(key, fields);
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long hincrBy(byte[] key, byte[] field, long value) {
		Long ret = master.hincrBy(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hincrBy(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double hincrByFloat(byte[] key, byte[] field, double value) {
		Double ret = master.hincrByFloat(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hincrByFloat(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Boolean hexists(byte[] key, byte[] field) {
		if (master.hexists(key, field)) {
			return true;
		} else if (master.exists(key)) {
			return false;
		}
		for (JedisClient s : slaves) {
			try {
				if (s.hexists(key, field)) {
					return true;
				} else if (s.exists(key)) {
					return false;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public Long hdel(byte[] key, byte[]... field) {
		Long ret = master.hdel(key, field);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hdel(key, field);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;

	}

	@Override
	public Long hlen(byte[] key) {
		Long ret;
		if ((ret = master.hlen(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hlen(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Set<byte[]> hkeys(byte[] key) {
		Set<byte[]> ret;
		if ((ret = master.hkeys(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hkeys(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Collection<byte[]> hvals(byte[] key) {
		Collection<byte[]> ret;
		if ((ret = master.hvals(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hvals(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		Map<byte[], byte[]> ret;
		if ((ret = master.hgetAll(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hgetAll(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public Long rpush(byte[] key, byte[]... args) {
		Long ret = master.rpush(key, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpush(key, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;

	}

	@Override
	public Long lpush(byte[] key, byte[]... args) {
		Long ret = master.lpush(key, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lpush(key, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;

	}

	@Override
	public Long llen(byte[] key) {
		Long ret;
		if ((ret = master.llen(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.llen(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public List<byte[]> lrange(byte[] key, long start, long end) {
		List<byte[]> ret;
		if ((ret = master.lrange(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.lrange(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public String ltrim(byte[] key, long start, long end) {
		String ret = master.ltrim(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.ltrim(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] lindex(byte[] key, long index) {
		byte[] ret;
		if ((ret = master.lindex(key, index)) != null) {
			return ret;
		} else if (master.exists(key)) {
			return null;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.lindex(key, index)) != null) {
					return ret;
				} else if (s.exists(key)) {
					return null;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public String lset(byte[] key, long index, byte[] value) {
		String ret = master.lset(key, index, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lset(key, index, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long lrem(byte[] key, long count, byte[] value) {
		Long ret = master.lrem(key, count, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lrem(key, count, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] lpop(byte[] key) {
		byte[] ret = master.lpop(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lpop(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] rpop(byte[] key) {
		byte[] ret = master.rpop(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpop(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long sadd(byte[] key, byte[]... member) {
		Long ret = master.sadd(key, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.sadd(key, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<byte[]> smembers(byte[] key) {
		Set<byte[]> ret;
		if ((ret = master.smembers(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.smembers(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long srem(byte[] key, byte[]... member) {
		Long ret = master.srem(key, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.srem(key, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] spop(byte[] key) {
		byte[] ret = master.spop(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.spop(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<byte[]> spop(byte[] key, long count) {
		Set<byte[]> ret = master.spop(key, count);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.spop(key, count);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long scard(byte[] key) {
		Long ret;
		if ((ret = master.scard(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.scard(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Boolean sismember(byte[] key, byte[] member) {
		if (master.exists(key)) {
			return master.sismember(key, member);
		}
		for (JedisClient s : slaves) {
			try {
				if (s.exists(key)) {
					return s.sismember(key, member);
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public byte[] srandmember(byte[] key) {
		byte[] ret;
		if ((ret = master.srandmember(key)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.srandmember(key)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public List<byte[]> srandmember(byte[] key, int count) {
		List<byte[]> ret;
		if ((ret = master.srandmember(key, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.srandmember(key, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long strlen(byte[] key) {
		Long ret;
		if ((ret = master.strlen(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.strlen(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long zadd(byte[] key, double score, byte[] member) {
		Long ret = master.zadd(key, score, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, score, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zadd(byte[] key, double score, byte[] member, ZAddParams params) {
		Long ret = master.zadd(key, score, member, params);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, score, member, params);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
		Long ret = master.zadd(key, scoreMembers);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, scoreMembers);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
		Long ret = master.zadd(key, scoreMembers, params);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, scoreMembers, params);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<byte[]> zrange(byte[] key, long start, long end) {

		Set<byte[]> ret;
		if ((ret = master.zrange(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrange(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zrem(byte[] key, byte[]... member) {
		Long ret = master.zrem(key, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zrem(key, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double zincrby(byte[] key, double score, byte[] member) {
		Double ret = master.zincrby(key, score, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zincrby(key, score, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double zincrby(byte[] key, double score, byte[] member, ZIncrByParams params) {
		Double ret = master.zincrby(key, score, member, params);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zincrby(key, score, member, params);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zrank(byte[] key, byte[] member) {
		Long ret;
		if ((ret = master.zrank(key, member)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrank(key, member)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long zrevrank(byte[] key, byte[] member) {
		Long ret;
		if ((ret = master.zrevrank(key, member)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrank(key, member)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Set<byte[]> zrevrange(byte[] key, long start, long end) {
		Set<byte[]> ret;
		if ((ret = master.zrevrange(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrange(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
		Set<Tuple> ret;
		if ((ret = master.zrangeWithScores(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeWithScores(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(byte[] key, long start, long end) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeWithScores(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeWithScores(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zcard(byte[] key) {
		Long ret;
		if ((ret = master.zcard(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zcard(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Double zscore(byte[] key, byte[] member) {
		Double ret;
		if ((ret = master.zscore(key, member)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zscore(key, member)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public List<byte[]> sort(byte[] key) {
		List<byte[]> ret;
		if ((ret = master.sort(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.sort(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		List<byte[]> ret;
		if ((ret = master.sort(key, sortingParameters)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.sort(key, sortingParameters)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public Long zcount(byte[] key, double min, double max) {
		Long ret;
		if ((ret = master.zcount(key, min, max)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zcount(key, min, max)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Long zcount(byte[] key, byte[] min, byte[] max) {
		Long ret;
		if ((ret = master.zcount(key, min, max)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zcount(key, min, max)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
		Set<byte[]> ret;
		if ((ret = master.zrangeByScore(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
		Set<byte[]> ret;
		if ((ret = master.zrangeByScore(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
		Set<byte[]> ret;
		if ((ret = master.zrevrangeByScore(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
		Set<byte[]> ret;
		if ((ret = master.zrangeByScore(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
		Set<byte[]> ret;
		if ((ret = master.zrevrangeByScore(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
		Set<byte[]> ret;
		if ((ret = master.zrangeByScore(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
		Set<byte[]> ret;
		if ((ret = master.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
		Set<byte[]> ret;
		if ((ret = master.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zremrangeByRank(byte[] key, long start, long end) {
		Long ret = master.zremrangeByRank(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByRank(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zremrangeByScore(byte[] key, double start, double end) {
		Long ret = master.zremrangeByScore(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByScore(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zremrangeByScore(byte[] key, byte[] start, byte[] end) {
		Long ret = master.zremrangeByScore(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByScore(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zlexcount(byte[] key, byte[] min, byte[] max) {
		Long ret;
		if ((ret = master.zlexcount(key, min, max)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zlexcount(key, min, max)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
		Set<byte[]> ret;
		if ((ret = master.zrangeByLex(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByLex(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
		Set<byte[]> ret;
		if ((ret = master.zrangeByLex(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByLex(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
		Set<byte[]> ret;
		if ((ret = master.zrevrangeByLex(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByLex(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
		Set<byte[]> ret;
		if ((ret = master.zrevrangeByLex(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByLex(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
		Long ret = master.zremrangeByLex(key, min, max);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByLex(key, min, max);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
		Long ret = master.linsert(key, where, pivot, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.linsert(key, where, pivot, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long lpushx(byte[] key, byte[]... arg) {
		Long ret = master.lpushx(key, arg);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lpushx(key, arg);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long rpushx(byte[] key, byte[]... arg) {
		Long ret = master.rpushx(key, arg);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpushx(key, arg);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public List<byte[]> blpop(byte[] arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<byte[]> brpop(byte[] arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long del(byte[] key) {
		Long ret = master.del(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.del(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] echo(byte[] arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long move(byte[] key, int dbIndex) {
		Long ret = master.move(key, dbIndex);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.move(key, dbIndex);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long bitcount(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long bitcount(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long pfadd(byte[] key, byte[]... elements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long pfcount(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long geoadd(byte[] key, Map<byte[], GeoCoordinate> memberCoordinateMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<byte[]> geohash(byte[] key, byte[]... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoCoordinate> geopos(byte[] key, byte[]... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit, GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<byte[]> sscan(byte[] key, byte[] cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<byte[]> sscan(byte[] key, byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Tuple> zscan(byte[] key, byte[] cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Tuple> zscan(byte[] key, byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<byte[]> bitfield(byte[] key, byte[]... arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long del(byte[]... keys) {
		Long ret = master.del(keys);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.del(keys);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long exists(byte[]... keys) {
		Long ret;
		if ((ret = master.exists(keys)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.exists(keys)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public List<byte[]> blpop(int timeout, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<byte[]> brpop(int timeout, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<byte[]> blpop(byte[]... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<byte[]> brpop(byte[]... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<byte[]> keys(byte[] pattern) {
		Set<byte[]> ret;
		if ((ret = master.keys(pattern)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.keys(pattern)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public List<byte[]> mget(byte[]... keys) {
		List<byte[]> ret;
		if ((ret = master.mget(keys)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.mget(keys)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public String mset(byte[]... keysvalues) {
		String ret = master.mset(keysvalues);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.mset(keysvalues);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long msetnx(byte[]... keysvalues) {
		Long ret = master.msetnx(keysvalues);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.msetnx(keysvalues);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String rename(byte[] oldkey, byte[] newkey) {
		String ret = master.rename(oldkey, newkey);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rename(oldkey, newkey);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long renamenx(byte[] oldkey, byte[] newkey) {
		Long ret = master.renamenx(oldkey, newkey);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.renamenx(oldkey, newkey);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		byte[] ret = master.rpoplpush(srckey, dstkey);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpoplpush(srckey, dstkey);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<byte[]> sdiff(byte[]... keys) {
		Set<byte[]> ret;
		if ((ret = master.sdiff(keys)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.sdiff(keys)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long sdiffstore(byte[] dstkey, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<byte[]> sinter(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sinterstore(byte[] dstkey, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long smove(byte[] srckey, byte[] dstkey, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sort(byte[] key, SortingParams sortingParameters, byte[] dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sort(byte[] key, byte[] dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<byte[]> sunion(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sunionstore(byte[] dstkey, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String watch(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String unwatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zinterstore(byte[] dstkey, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zinterstore(byte[] dstkey, ZParams params, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zunionstore(byte[] dstkey, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zunionstore(byte[] dstkey, ZParams params, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long publish(byte[] channel, byte[] message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] randomBinaryKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long bitop(BitOP op, byte[] destKey, byte[]... srcKeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String pfmerge(byte[] destkey, byte[]... sourcekeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long pfcount(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String set(String key, String value) {
		String ret = master.set(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.set(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		String ret = master.set(key, value, nxxx, expx, time);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.set(key, value, nxxx, expx, time);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String set(String key, String value, String nxxx) {
		String ret = master.set(key, value, nxxx);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.set(key, value, nxxx);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String get(String key) {
		String ret;
		if ((ret = master.get(key)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.get(key)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Boolean exists(String key) {
		if (master.exists(key)) {
			return true;
		}
		for (JedisClient s : slaves) {
			try {
				if (s.exists(key)) {
					return true;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public Long persist(String key) {
		Long ret = master.persist(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.persist(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String type(String key) {
		String ret;
		if (!NONE.equals(ret = master.type(key))) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if (!NONE.equals(ret = s.type(key))) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return NONE;
	}

	@Override
	public Long expire(String key, int seconds) {
		Long ret = master.expire(key, seconds);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.expire(key, seconds);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		Long ret = master.expireAt(key, unixTime);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.expireAt(key, unixTime);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		Long ret = master.pexpireAt(key, millisecondsTimestamp);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.pexpireAt(key, millisecondsTimestamp);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long ttl(String key) {
		Long ret;
		if ((ret = master.ttl(key)) != LNIL) {
			return ret;
		} else if (master.exists(key)) {
			return LNIL;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.ttl(key)) != LNIL) {
					return ret;
				} else if (s.exists(key)) {
					return LNIL;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return LNIL;
	}

	@Override
	public Long pttl(String key) {
		Long ret;
		if ((ret = master.pttl(key)) != LNIL) {
			return ret;
		} else if (master.exists(key)) {
			return LNIL;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.pttl(key)) != LNIL) {
					return ret;
				} else if (s.exists(key)) {
					return LNIL;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return LNIL;
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean getbit(String key, long offset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSet(String key, String value) {
		String ret = master.getSet(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.getSet(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long setnx(String key, String value) {
		Long ret = master.setnx(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setnx(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String setex(String key, int seconds, String value) {
		String ret = master.setex(key, seconds, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.setex(key, seconds, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {
		String ret = master.psetex(key, milliseconds, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.psetex(key, milliseconds, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long decrBy(String key, long integer) {
		Long ret = master.decrBy(key, integer);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.decrBy(key, integer);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long decr(String key) {
		Long ret = master.decr(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.decr(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long incrBy(String key, long integer) {
		Long ret = master.incrBy(key, integer);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.incrBy(key, integer);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double incrByFloat(String key, double value) {
		Double ret = master.incrByFloat(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.incrByFloat(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long incr(String key) {
		Long ret = master.incr(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.incr(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long append(String key, String value) {
		Long ret = master.append(key, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.append(key, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String substr(String key, int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long hset(String key, String field, String value) {
		Long ret = master.hset(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hset(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String hget(String key, String field) {
		String ret;
		if ((ret = master.hget(key, field)) != null) {
			return ret;
		} else if (master.exists(key)) {
			return null;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hget(key, field)) != null) {
					return ret;
				} else if (s.exists(key)) {
					return null;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		Long ret = master.hsetnx(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hsetnx(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		String ret = master.hmset(key, hash);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hmset(key, hash);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		if (master.exists(key)) {
			return master.hmget(key, fields);
		}
		for (JedisClient s : slaves) {
			try {
				if (s.exists(key)) {
					return s.hmget(key, fields);
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		Long ret = master.hincrBy(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hincrBy(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double hincrByFloat(String key, String field, double value) {
		Double ret = master.hincrByFloat(key, field, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hincrByFloat(key, field, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Boolean hexists(String key, String field) {
		if (master.hexists(key, field)) {
			return true;
		} else if (master.exists(key)) {
			return false;
		}
		for (JedisClient s : slaves) {
			try {
				if (s.hexists(key, field)) {
					return true;
				} else if (s.exists(key)) {
					return false;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public Long hdel(String key, String... field) {
		Long ret = master.hdel(key, field);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.hdel(key, field);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long hlen(String key) {
		Long ret;
		if ((ret = master.hlen(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hlen(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Set<String> hkeys(String key) {
		Set<String> ret;
		if ((ret = master.hkeys(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hkeys(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public List<String> hvals(String key) {
		List<String> ret;
		if ((ret = master.hvals(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hvals(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Map<String, String> ret;
		if ((ret = master.hgetAll(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.hgetAll(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public Long rpush(String key, String... args) {
		Long ret = master.rpush(key, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpush(key, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long lpush(String key, String... args) {
		Long ret = master.lpush(key, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lpush(key, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long llen(String key) {
		Long ret;
		if ((ret = master.llen(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.llen(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		List<String> ret;
		if ((ret = master.lrange(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.lrange(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public String ltrim(String key, long start, long end) {
		String ret = master.ltrim(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.ltrim(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String lindex(String key, long index) {
		String ret;
		if ((ret = master.lindex(key, index)) != null) {
			return ret;
		} else if (master.exists(key)) {
			return null;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.lindex(key, index)) != null) {
					return ret;
				} else if (s.exists(key)) {
					return null;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public String lset(String key, long index, String value) {
		String ret = master.lset(key, index, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lset(key, index, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long lrem(String key, long count, String value) {
		Long ret = master.lrem(key, count, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lrem(key, count, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String lpop(String key) {
		String ret = master.lpop(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lpop(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String rpop(String key) {
		String ret = master.rpop(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpop(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long sadd(String key, String... member) {
		Long ret = master.sadd(key, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.sadd(key, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<String> smembers(String key) {
		Set<String> ret;
		if ((ret = master.smembers(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.smembers(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long srem(String key, String... member) {
		Long ret = master.srem(key, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.srem(key, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String spop(String key) {
		String ret = master.spop(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.spop(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<String> spop(String key, long count) {
		Set<String> ret = master.spop(key, count);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.spop(key, count);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long scard(String key) {
		Long ret;
		if ((ret = master.scard(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.scard(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Boolean sismember(String key, String member) {
		if (master.exists(key)) {
			return master.sismember(key, member);
		}
		for (JedisClient s : slaves) {
			try {
				if (s.exists(key)) {
					return s.sismember(key, member);
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return false;
	}

	@Override
	public String srandmember(String key) {
		String ret;
		if ((ret = master.srandmember(key)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.srandmember(key)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public List<String> srandmember(String key, int count) {
		List<String> ret;
		if ((ret = master.srandmember(key, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.srandmember(key, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long strlen(String key) {
		Long ret;
		if ((ret = master.strlen(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.strlen(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long zadd(String key, double score, String member) {
		Long ret = master.zadd(key, score, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, score, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {
		Long ret = master.zadd(key, score, member, params);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, score, member, params);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		Long ret = master.zadd(key, scoreMembers);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, scoreMembers);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		Long ret = master.zadd(key, scoreMembers, params);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zadd(key, scoreMembers, params);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		Set<String> ret;
		if ((ret = master.zrange(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrange(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zrem(String key, String... member) {
		Long ret = master.zrem(key, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zrem(key, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		Double ret = master.zincrby(key, score, member);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zincrby(key, score, member);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Double zincrby(String key, double score, String member, ZIncrByParams params) {
		Double ret = master.zincrby(key, score, member, params);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zincrby(key, score, member, params);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zrank(String key, String member) {
		Long ret;
		if ((ret = master.zrank(key, member)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrank(key, member)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Long zrevrank(String key, String member) {
		Long ret;
		if ((ret = master.zrevrank(key, member)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrank(key, member)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		Set<String> ret;
		if ((ret = master.zrevrange(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrange(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		Set<Tuple> ret;
		if ((ret = master.zrangeWithScores(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeWithScores(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeWithScores(key, start, end)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeWithScores(key, start, end)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zcard(String key) {
		Long ret;
		if ((ret = master.zcard(key)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zcard(key)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Double zscore(String key, String member) {
		Double ret;
		if ((ret = master.zscore(key, member)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zscore(key, member)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public List<String> sort(String key) {
		List<String> ret;
		if ((ret = master.sort(key)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.sort(key)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		List<String> ret;
		if ((ret = master.sort(key, sortingParameters)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.sort(key, sortingParameters)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public Long zcount(String key, double min, double max) {
		Long ret;
		if ((ret = master.zcount(key, min, max)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zcount(key, min, max)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Long zcount(String key, String min, String max) {
		Long ret;
		if ((ret = master.zcount(key, min, max)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zcount(key, min, max)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		Set<String> ret;
		if ((ret = master.zrangeByScore(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		Set<String> ret;
		if ((ret = master.zrangeByScore(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		Set<String> ret;
		if ((ret = master.zrevrangeByScore(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		Set<String> ret;
		if ((ret = master.zrangeByScore(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		Set<String> ret;
		if ((ret = master.zrevrangeByScore(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		Set<String> ret;
		if ((ret = master.zrangeByScore(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScore(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		Set<String> ret;
		if ((ret = master.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		Set<String> ret;
		if ((ret = master.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScore(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByScoreWithScores(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		Set<Tuple> ret;
		if ((ret = master.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByScoreWithScores(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		Long ret = master.zremrangeByRank(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByRank(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		Long ret = master.zremrangeByScore(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByScore(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		Long ret = master.zremrangeByScore(key, start, end);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByScore(key, start, end);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		Long ret;
		if ((ret = master.zlexcount(key, min, max)) > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zlexcount(key, min, max)) > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return 0L;
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		Set<String> ret;
		if ((ret = master.zrangeByLex(key, min, max)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByLex(key, min, max)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		Set<String> ret;
		if ((ret = master.zrangeByLex(key, min, max, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrangeByLex(key, min, max, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {
		Set<String> ret;
		if ((ret = master.zrevrangeByLex(key, max, min)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByLex(key, max, min)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		Set<String> ret;
		if ((ret = master.zrevrangeByLex(key, max, min, offset, count)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.zrevrangeByLex(key, max, min, offset, count)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		Long ret = master.zremrangeByLex(key, min, max);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.zremrangeByLex(key, min, max);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		Long ret = master.linsert(key, where, pivot, value);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.linsert(key, where, pivot, value);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long lpushx(String key, String... arg) {
		Long ret = master.lpushx(key, arg);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.lpushx(key, arg);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long rpushx(String key, String... arg) {
		Long ret = master.rpushx(key, arg);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpushx(key, arg);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public List<String> blpop(String arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> blpop(int timeout, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> brpop(String arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long del(String key) {
		Long ret = master.del(key);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.del(key);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String echo(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long move(String key, int dbIndex) {
		Long ret = master.move(key, dbIndex);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.move(key, dbIndex);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long bitcount(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long bitpos(String key, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long bitpos(String key, boolean value, BitPosParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<String> sscan(String key, int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Tuple> zscan(String key, int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long pfadd(String key, String... elements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long pfcount(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long geoadd(String key, double longitude, double latitude, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double geodist(String key, String member1, String member2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double geodist(String key, String member1, String member2, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> geohash(String key, String... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoCoordinate> geopos(String key, String... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Long> bitfield(String key, String... arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long del(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long exists(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> blpop(String... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> brpop(String... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keys(String pattern) {
		Set<String> ret;
		if ((ret = master.keys(pattern)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.keys(pattern)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public List<String> mget(String... keys) {
		List<String> ret;
		if ((ret = master.mget(keys)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.mget(keys)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public String mset(String... keysvalues) {
		String ret = master.mset(keysvalues);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.mset(keysvalues);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long msetnx(String... keysvalues) {
		Long ret = master.msetnx(keysvalues);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.msetnx(keysvalues);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String rename(String oldkey, String newkey) {
		String ret = master.rename(oldkey, newkey);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rename(oldkey, newkey);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		Long ret = master.renamenx(oldkey, newkey);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.renamenx(oldkey, newkey);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		String ret = master.rpoplpush(srckey, dstkey);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.rpoplpush(srckey, dstkey);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public Set<String> sdiff(String... keys) {
		Set<String> ret;
		if ((ret = master.sdiff(keys)).size() > 0) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.sdiff(keys)).size() > 0) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> sinter(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sort(String key, String dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> sunion(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String watch(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zinterstore(String dstkey, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zunionstore(String dstkey, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long publish(String channel, String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String randomKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<String> scan(int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<String> scan(String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScanResult<String> scan(String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long pfcount(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Object> syncAndReturnAll(PipelineCallback callback, Object... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sync(PipelineCallback callback, Object... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Response<?>> execGetResponse(TransactionCallback callback, Object... args) {
		List<Response<?>> ret = master.execGetResponse(callback, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.execGetResponse(callback, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public void exec(TransactionCallback callback, Object... args) {
		master.exec(callback, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.exec(callback, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
	}

	@Override
	public <T> T submit(JedisCallback<T> callback, Object... args) {
		T ret = master.submit(callback, args);
		excutor.submit(new Runnable() {
			public void run() {
				for (JedisClient s : slaves) {
					try {
						s.submit(callback, args);
					} catch (Exception e) {
						if (ignoreError) {
							logger.info("proxy client ignore error " + e.getMessage());
						} else {
							throw e;
						}
					}
				}
			}
		});
		return ret;
	}

	@Override
	public JedisPool getJedisPool() {
		return master.getJedisPool();
	}

	@Override
	@Deprecated
	public boolean tryLock(String key, int expireSeconds) {
		return master.tryLock(key, expireSeconds);
	}

	@Override
	public boolean tryLock(String key, String val, int expireSeconds) {
		return master.tryLock(key, val, expireSeconds);
	}

	@Override
	@Deprecated
	public boolean tryLock(String key, int expireSeconds, long waitMillis, int tryTimes) {
		return master.tryLock(key, expireSeconds, waitMillis, tryTimes);
	}

	@Override
	public boolean tryLock(String key, String val, int expireSeconds, long waitMillis, int tryTimes) {
		return master.tryLock(key, val, expireSeconds, waitMillis, tryTimes);
	}

	@Override
	@Deprecated
	public void unlock(String key) {
		master.unlock(key);
	}

	@Override
	public int unlock(String key, String val) {
		return master.unlock(key, val);
	}

	@Override
	public boolean tryHostLock(String key, int expireSeconds) {
		return master.tryHostLock(key, expireSeconds);
	}

	@Override
	public byte[] getex(byte[] key, int expirs) {
		byte[] ret;
		if ((ret = master.getex(key, expirs)) != null) {
			return ret;
		}
		for (JedisClient s : slaves) {
			try {
				if ((ret = s.getex(key, expirs)) != null) {
					return ret;
				}
			} catch (Exception e) {
				if (ignoreError) {
					logger.info("proxy client ignore error " + e.getMessage());
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean scriptExists(String sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> scriptExists(String... sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String scriptLoad(String script) {
		// TODO Auto-generated method stub
		return null;
	}

}
