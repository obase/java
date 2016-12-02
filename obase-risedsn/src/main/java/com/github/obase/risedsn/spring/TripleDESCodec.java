package com.github.obase.risedsn.spring;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public final class TripleDESCodec {

	static final SecureRandom SECURE_RANDOM = new SecureRandom();

	protected static final byte[] codec(int mode, String password, byte[] bytes) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		if (password.length() != DESedeKeySpec.DES_EDE_KEY_LEN) {
			throw new IllegalArgumentException("3DES的密码长度必须是24位");
		}

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey securekey = keyFactory.generateSecret(new DESedeKeySpec(password.getBytes()));

		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(mode, securekey, SECURE_RANDOM);
		return cipher.doFinal(bytes);
	}

	public static byte[] encrypt(String password, byte[] src) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return codec(Cipher.ENCRYPT_MODE, password, src);
	}

	public static byte[] decrypt(String password, byte[] dst) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return codec(Cipher.DECRYPT_MODE, password, dst);
	}

	public static String encrypt(String password, String srcStr, String charset) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		byte[] src = charset == null ? srcStr.getBytes() : srcStr.getBytes(charset);
		byte[] dst = codec(Cipher.ENCRYPT_MODE, password, src);
		return charset == null ? new String(dst) : new String(dst, charset);
	}
	
	public static byte[] encryptByte(String password, String srcStr, String charset) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		byte[] src = charset == null ? srcStr.getBytes() : srcStr.getBytes(charset);
		byte[] dst = codec(Cipher.ENCRYPT_MODE, password, src);
		return dst;
	}

	public static String decrypt(String password, String dstStr, String charset) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		byte[] dst = charset == null ? dstStr.getBytes() : dstStr.getBytes(charset);
		byte[] src = codec(Cipher.DECRYPT_MODE, password, dst);
		return charset == null ? new String(src) : new String(src, charset);
	}

}
