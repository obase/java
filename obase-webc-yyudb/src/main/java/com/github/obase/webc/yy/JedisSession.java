package com.github.obase.webc.yy;

import java.util.Scanner;

import com.github.obase.security.Principal;
import com.github.obase.webc.WsidSession;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public final class JedisSession implements WsidSession {

	protected final JedisPool jedisPool;
	protected final boolean master;

	public JedisSession(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
		this.master = "master".equals(role());
	}

	private String role() {
		Jedis jedis = null;
		String replication = null;
		try {
			jedis = jedisPool.getResource();
			replication = jedis.info("Replication");
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		Scanner scan = null;
		try {
			scan = new Scanner(replication);
			for (String line = null; scan.hasNextLine();) {
				line = scan.nextLine();
				if (line.startsWith("role")) {
					int pos = line.indexOf(':');
					if (pos > 0) {
						return line.substring(pos + 1);
					}
				}
			}
		} finally {
			if (scan != null)
				scan.close();
		}
		return null;
	}

	public boolean isMaster() {
		return master;
	}

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

}
