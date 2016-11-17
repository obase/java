package com.github.obase.crypto;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.github.obase.coding.Base64;

/**
 * 与go实现相对应，可以相互加密解密
 */
public class AES {

	public static byte[] encrypt(String key, byte[] origData) throws GeneralSecurityException {

		byte[] keyBytes = getKeyBytes(key);
		byte[] buf = new byte[16];
		System.arraycopy(keyBytes, 0, buf, 0, keyBytes.length > buf.length ? keyBytes.length : buf.length);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(buf, "AES"), new IvParameterSpec(keyBytes));
		return cipher.doFinal(origData);

	}

	public static byte[] decrypt(String key, byte[] crypted) throws GeneralSecurityException {
		byte[] keyBytes = getKeyBytes(key);
		byte[] buf = new byte[16];
		System.arraycopy(keyBytes, 0, buf, 0, keyBytes.length > buf.length ? keyBytes.length : buf.length);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(buf, "AES"), new IvParameterSpec(keyBytes));
		return cipher.doFinal(crypted);
	}

	private static byte[] getKeyBytes(String key) {
		byte[] bytes = key.getBytes();
		return bytes.length == 16 ? bytes : Arrays.copyOf(bytes, 16);
	}

	public static String encrypt(String key, String val) throws GeneralSecurityException {
		byte[] origData = val.getBytes();
		byte[] crypted = encrypt(key, origData);
		return Base64.Encoder.RFC4648.encodeToString(crypted);
	}

	public static String decrypt(String key, String val) throws GeneralSecurityException {
		byte[] crypted = Base64.Decoder.RFC4648.decode(val);
		byte[] origData = decrypt(key, crypted);
		return new String(origData);
	}

	public static String encryptUrlSafe(String key, String val) throws GeneralSecurityException {
		byte[] origData = val.getBytes();
		byte[] crypted = encrypt(key, origData);
		return Base64.Encoder.RFC4648_URLSAFE.encodeToString(crypted);
	}

	public static String decryptUrlSafe(String key, String val) throws GeneralSecurityException {
		byte[] crypted = Base64.Decoder.RFC4648_URLSAFE.decode(val);
		byte[] origData = decrypt(key, crypted);
		return new String(origData);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			System.err.print("Usage: java AES (-e|-d) <key> <content>");
		}
		if ("-e".equals(args[0])) {
			System.out.println(encrypt(args[1], args[2]));
		} else if ("-d".equals(args[0])) {
			System.out.println(decrypt(args[1], args[2]));
		} else {
			System.err.print("Usage: java AES (-e|-d) <key> <content>");
		}
	}

}