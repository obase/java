package com.github.obase.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.obase.coding.Base64;
import com.github.obase.coding.Hex;

public class SHA256 {

	public static final ThreadLocal<MessageDigest> LOCAL = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("SHA256");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("algorythm unsupported SHA256");
			}
		}

	};

	private SHA256() {
	}

	public static long hash64(String key) {
		return hash64(key.getBytes());
	}

	public static long hash64(byte[] key) {
		byte[] bKey = hash64AsBytes(key);
		long res = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
		return res;
	}

	public static byte[] hash64AsBytes(byte[] key) {
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(key);
		byte[] bytes = alg.digest();
		return bytes;
	}

	public static String hashToHex(String key) {
		return hashToHex(key.getBytes());
	}

	public static String hashToHex(byte[] key) {
		byte[] bKey = hash64AsBytes(key);
		return Hex.encode(bKey);
	}

	public static String hashToBase64(String key) {
		return hashToBase64(key.getBytes());
	}

	public static String hashToBase64(byte[] key) {
		byte[] bKey = hash64AsBytes(key);
		return Base64.Encoder.RFC4648_URLSAFE.encodeToString(bKey);
	}
}
