package com.github.obase.webc.yy;

import com.github.obase.webc.WsidSession;

import redis.clients.jedis.JedisPool;

public class ClstrJedisSession implements WsidSession {

	final JedisSession master;
	final JedisSession[] nodes;

	public ClstrJedisSession(JedisPool... pools) {
		nodes = new JedisSession[pools.length];
		int md = -1;
		for (int i = 0; i < pools.length; i++) {
			nodes[i] = new JedisSession(pools[i]);
			if (md < 0 && nodes[i].master) {
				md = i;
			}
		}
		master = md == -1 ? null : nodes[md];
	}

	@Override
	public void passivate(String key, String data, long expireMillis) {
		master.passivate(key, data, expireMillis);
	}

	@Override
	public String activate(String key, long expireMillis) {
		String data = null;
		for (int i = 0; data == null && i < nodes.length; i++) {
			data = nodes[i].activate(key, expireMillis);
		}
		return data;
	}

}
