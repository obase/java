package com.obase.loader.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.crypto.AES;
import com.obase.loader.EncryClassLoader;
import com.obase.loader.LoaderErrno;

public class Aes128ClassLoader extends EncryClassLoader {

	public static final String LOADER_PASSWD_FILE = "LOADER_PASSWD_FILE";
	public static final int AES128_SWAP_SIZE = 8;

	protected final String passwd;

	public Aes128ClassLoader() {
		this.passwd = readLoaderPasswd();
	}

	private static String readLoaderPasswd() {

		File file = null;
		String path = System.getProperty(LOADER_PASSWD_FILE);
		if (path != null) {
			file = new File(path);
		} else {
			String home = System.getProperty("user.home");
			file = new File(home, ".LOADER_PASSWD_FILE");
		}
		if (!file.exists()) {
			throw new MessageException(LoaderErrno.SOURCE, LoaderErrno.LOADER_PASSWD_FILE_NOT_FOUND, "Loader passwd file not found! " + file.getAbsolutePath());
		}

		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			in = new BufferedReader(new FileReader(file));
			for (String line = null; (line = in.readLine()) != null;) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			throw new WrappedException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new WrappedException(e);
				}
			}
		}
	}

	@Override
	protected byte[] encrptBytes(byte[] bytes) throws Exception {
		byte[] result = AES.encrypt(passwd, bytes);
		swap(result, AES128_SWAP_SIZE);
		return result;
	}

	@Override
	protected byte[] decrptBytes(byte[] bytes) throws Exception {
		byte[] result = AES.decrypt(passwd, bytes);
		swap(result, AES128_SWAP_SIZE);
		return result;
	}

	private static void swap(byte[] bytes, int size) {
		if (bytes.length > size) {
			byte tmp;
			for (int i = 0, j = bytes.length - 1; i < AES128_SWAP_SIZE; i++, j--) {
				tmp = bytes[i];
				bytes[i] = bytes[j];
				bytes[j] = tmp;
			}
		}
	}
}
