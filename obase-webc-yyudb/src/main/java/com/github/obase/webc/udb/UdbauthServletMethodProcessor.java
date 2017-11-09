package com.github.obase.webc.udb;

import com.github.obase.webc.WsidSession;
import com.github.obase.webc.yy.JedisSession;

import redis.clients.jedis.JedisPool;

public abstract class UdbauthServletMethodProcessor extends UdbauthServletMethodProcessor2 {

	protected final WsidSession wsidSession = new JedisSession(getJedisPool());

	@Override
	protected final WsidSession getWsidSession() {
		return wsidSession;
	}

	protected abstract JedisPool getJedisPool();

}
