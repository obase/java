package com.obase.loader.test;

import java.io.File;

import com.obase.loader.support.Aes128ClassLoader;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File srcZipFile = new File("D:/Workspace/dbms/trunk/dbms-web/target/dbms-web-1.0.0-SNAPSHOT.war");
		File dstZipFile = new File("D:/test.war");
		if (dstZipFile.exists()) {
			dstZipFile.delete();
		}

		Aes128ClassLoader loader = new Aes128ClassLoader();
		loader.encZipFile(srcZipFile, dstZipFile);
	}
}
