package com.github.obase.webc.hiido;

import com.github.obase.security.Principal;
import com.github.obase.webc.WsidSession;
import com.github.obase.webc.yy.UserPrincipal;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 * @deprecated please used HiidoauthServletMethodProcessor2 instead of for convenient
 */
@Deprecated
public abstract class HiidoauthServletMethodProcessor extends HiidoauthServletMethodProcessor2 {

	protected final WsidSession wsidSession = new WsidSession() {

		final JedisPool jedisPool = getJedisPool();

		@Override
		public void passivate(String key, Principal val, long expireMillis) {
			String data = ((UserPrincipal) val).encode();
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.psetex(key, expireMillis, data);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		@Override
		public Principal activate(String key, long expireMillis) {
			Jedis jedis = null;
			Response<String> resp = null;
			try {
				jedis = jedisPool.getResource();
				Transaction tx = jedis.multi();
				resp = tx.get(key);
				tx.pexpire(key, expireMillis);
				tx.exec();

			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
			String data = resp.get();
			return data == null ? null : new UserPrincipal().decode(data);
		}
	};

	@Override
	protected final WsidSession getWsidSession() {
		return wsidSession;
	}

	protected abstract JedisPool getJedisPool();

}
