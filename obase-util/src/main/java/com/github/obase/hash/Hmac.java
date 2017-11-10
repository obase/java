package com.github.obase.hash;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.github.obase.coding.Hex;

public class Hmac {
	static final Charset UTF8 = Charset.forName("utf8");

	static final String HmacMD5 = "HmacMD5";

	public static String hmacMD5Hex(String secret, String... args) throws GeneralSecurityException {
		SecretKeySpec sks = new SecretKeySpec(secret.getBytes(UTF8), HmacMD5);
		Mac mac = Mac.getInstance(HmacMD5);
		mac.init(sks);
		for (String arg : args) {
			mac.update(arg.getBytes(UTF8));
		}
		return Hex.encode(mac.doFinal());
	}

	static final String HmacSHA1 = "HmacSHA1";

	public static String hmacSHA1Hex(String secret, String... args) throws GeneralSecurityException {
		SecretKeySpec sks = new SecretKeySpec(secret.getBytes(UTF8), HmacSHA1);
		Mac mac = Mac.getInstance(HmacSHA1);
		mac.init(sks);
		for (String arg : args) {
			mac.update(arg.getBytes(UTF8));
		}
		return Hex.encode(mac.doFinal());
	}

	static final String HmacSHA256 = "HmacSHA256";

	public static String hmacSHA256Hex(String secret, String... args) throws GeneralSecurityException {
		SecretKeySpec sks = new SecretKeySpec(secret.getBytes(UTF8), HmacSHA256);
		Mac mac = Mac.getInstance(HmacSHA256);
		mac.init(sks);
		for (String arg : args) {
			mac.update(arg.getBytes(UTF8));
		}
		return Hex.encode(mac.doFinal());
	}
}
