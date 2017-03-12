package com.obase.loader.support;

import com.github.obase.crypto.AES;
import com.obase.loader.CryptoClassLoader;

public final class Aes128CryptoClassLoader extends CryptoClassLoader {

	@Override
	protected byte[] encrptBytes(String passwd, byte[] bytes) throws Exception {
		salt(bytes);
		bytes = AES.encrypt(passwd, bytes);
		return bytes;
	}

	@Override
	protected byte[] decrptBytes(String passwd, byte[] bytes) throws Exception {
		bytes = AES.decrypt(passwd, bytes);
		salt(bytes);
		return bytes;
	}

	private static void salt(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ~bytes[i];
		}
	}
}
