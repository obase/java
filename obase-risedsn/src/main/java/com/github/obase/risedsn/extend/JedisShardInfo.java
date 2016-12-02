package com.github.obase.risedsn.extend;

public class JedisShardInfo extends redis.clients.jedis.JedisShardInfo {

	final int db;

	public JedisShardInfo(String name, int weight, String host, int port, String password, int connectionTimeout, int soTimeout, int db) {
		super(host, name, port, soTimeout, weight);
		super.setConnectionTimeout(connectionTimeout);
		super.setPassword(password);
		super.setSoTimeout(soTimeout);
		this.db = db;
	}

	@Override
	public int getDb() {
		return db;
	}

}
