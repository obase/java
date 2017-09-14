package com.huya.dbms.test;

import java.io.File;

import com.obase.loader.support.Aes128CryptoClassLoader;

public class TestTool {

	public static void main(String[] args) throws Exception {
		File srcZipFile = new File("D:/Workspace/dbms/trunk/dbms-web/target/dbms-web-1.0.0-SNAPSHOT.war");
		File dstZipFile = new File("D:/dbms-web-enc.war");
		if (dstZipFile.exists()) {
			dstZipFile.delete();
		}
		Aes128CryptoClassLoader loader = new Aes128CryptoClassLoader();
		loader.encZipFile(srcZipFile, dstZipFile);
		System.out.println("done...");
	}

}
