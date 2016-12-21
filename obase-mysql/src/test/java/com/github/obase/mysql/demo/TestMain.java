package com.github.obase.mysql.demo;

import java.io.IOException;

import com.github.obase.mysql.asm.AsmKit;

public class TestMain {

	public static void main(String[] args) throws IOException, ReflectiveOperationException {
		AsmKit.newJdbcAction(Session.class.getCanonicalName());
	}

}
