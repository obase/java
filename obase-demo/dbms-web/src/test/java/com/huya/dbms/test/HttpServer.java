package com.huya.dbms.test;

import java.io.File;

import com.github.obase.test.EmbedTomcat;

public class HttpServer {

	public static void main(String[] args) {
		// EmbedTomcat.start(8080);
		EmbedTomcat.start(80, 443, "tomcat", "tomcat", new File("../config/dev/tomcat.keystore").getAbsolutePath());
	}

}
