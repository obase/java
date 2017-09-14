package com.huya.dbms.taglib;

import java.util.UUID;

public final class Functions {

	public static String xPageId() {
		return UUID.randomUUID().toString();
	}

}
