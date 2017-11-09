package com.github.obase.webc.hiido;

import com.github.obase.webc.WsidSession;
import com.github.obase.webc.yy.JedisSession;

import redis.clients.jedis.JedisPool;

/**
 * @deprecated please used HiidoauthServletMethodProcessor2 instead of for convenient
 */
@Deprecated
public abstract class HiidoauthServletMethodProcessor extends HiidoauthServletMethodProcessor2 {

	protected final WsidSession wsidSession = new JedisSession(getJedisPool());

	@Override
	protected final WsidSession getWsidSession() {
		return wsidSession;
	}

	protected abstract JedisPool getJedisPool();

}
