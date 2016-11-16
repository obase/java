package com.github.obase.spring.transaction;

import java.sql.Connection;

import org.springframework.util.Assert;

public class SimpleConnectionHandle implements ConnectionHandle {

	private final Connection connection;

	public SimpleConnectionHandle(Connection connection) {
		Assert.notNull(connection, "Connection must not be null");
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public void releaseConnection(Connection con) {
	}

	@Override
	public String toString() {
		return "SimpleConnectionHandle: " + this.connection;
	}

}
