package com.obase.loader.test;

import java.io.File;

import com.github.obase.crypto.AES;
import com.obase.loader.EncryClassLoader;

public class TestMain {

	public static void main(String[] args) throws Exception {
		
	}

	static final String key = "12345678";

	static EncryClassLoader loader = new EncryClassLoader(null) {
		@Override
		protected byte[] encrptBytes(byte[] bytes) throws Exception {
			return AES.encrypt(key, bytes);
		}

		@Override
		protected byte[] decrptBytes(byte[] bytes) throws Exception {
			return null;
		}
	};

	public static void encZipFile() throws Exception {
		File srcZipFile = new File("D:/Workspace/dbms/trunk/dbms-web/target/dbms-web-1.0.0-SNAPSHOT.war");
		File dstZipFile = new File("D:/test.war");
		if (dstZipFile.exists()) {
			dstZipFile.delete();
		}
		loader.encZipFile(srcZipFile, dstZipFile);
	}
}
