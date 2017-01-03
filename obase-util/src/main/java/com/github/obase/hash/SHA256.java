package com.github.obase.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.obase.coding.Hex;
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

	public static byte[] hashv(byte[]... inputs) {
		MessageDigest alg = LOCAL.get();
		alg.reset();
		for (byte[] input : inputs) {
			alg.update(input);
		}
		byte[] output = alg.digest();
		return output;
	}

	public static String hash(String val) {
		byte[] input = val.getBytes();
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(input);
		byte[] output = alg.digest();
		return Hex.encode(output);
	}

	public static String hashv(String... vals) {
		MessageDigest alg = LOCAL.get();
		alg.reset();
		for (String val : vals) {
			alg.update(val.getBytes());
		}
		byte[] output = alg.digest();
		return Hex.encode(output);
	}

	public static String hashBase64(String val) {
		byte[] input = val.getBytes();
		MessageDigest alg = LOCAL.get();
		alg.reset();
		alg.update(input);
		byte[] output = alg.digest();
		return Encoder.RFC4648.encodeToString(output);
	}

	public static String hashBase64v(String... vals) {

		MessageDigest alg = LOCAL.get();
		alg.reset();
		for (String val : vals) {
			alg.update(val.getBytes());
		}
		byte[] output = alg.digest();
		return Encoder.RFC4648.encodeToString(output);
	}
}
