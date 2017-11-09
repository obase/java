package com.github.obase.webc.yy;

import com.github.obase.security.Principal;
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
		master = md > 0 ? nodes[md] : null;
	}

	@Override
	public void passivate(String key, Principal val, long expireMillis) {
		master.passivate(key, val, expireMillis);
	}

	@Override
	public Principal activate(String key, long expireMillis) {
		Principal princ = null;
		for (int i = 0; princ == null && i < nodes.length; i++) {
			princ = nodes[i].activate(key, expireMillis);
		}
		return princ;
	}

}
