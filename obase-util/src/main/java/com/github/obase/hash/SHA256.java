package com.github.obase.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.obase.coding.Base64.Encoder;

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

	public static byte[] hash(byte[] input) {
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(input);
		byte[] output = alg.digest();
		return output;
	}

	public static byte[] hash(byte[] input, int offset, int len) {
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(input, offset, len);
		byte[] output = alg.digest();
		return output;
	}

	public static String hash(String val) {
		byte[] input = val.getBytes();
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(input);
		byte[] output = alg.digest();
		return new String(output);
	}

	public static String hashBase64(String val) {
		byte[] input = val.getBytes();
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(input);
		byte[] output = alg.digest();
		return Encoder.RFC4648_URLSAFE.encodeToString(output);
	}
}
