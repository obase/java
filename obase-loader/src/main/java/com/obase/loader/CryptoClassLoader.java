package com.obase.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ClassUtils;

import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.kit.ClassKit;

public abstract class CryptoClassLoader extends ClassLoader implements BeanFactoryPostProcessor {

	static final String ENCRY_CLASS_SUFFIX = "____";
	static int BAOS_BUFF_SIZE = 4092;
	static final String CRYPTO_PASSWD_FILE = "crypto_passwd_file";

	final String passwd;

	protected CryptoClassLoader() {
		super(ClassUtils.getDefaultClassLoader());
		this.passwd = readCryptoPasswd();
	}

	@Override
	public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.setBeanClassLoader(this);
	}

	@Override
	protected final Class<?> findClass(String name) throws ClassNotFoundException {

		Class<?> result = null;
		try {
			result = super.findClass(name);
		} catch (ClassNotFoundException e) {
			InputStream in = null;
			try {
				in = ClassKit.getResourceAsStream(getEncryptClassPath(name));
				if (in != null) {
					byte[] bytes = decrptBytes(passwd, readStreamBytes(in));
					return super.defineClass(name, bytes, 0, bytes.length);
				} else {
					throw e;
				}
			} catch (Exception ioe) {
				throw new ClassNotFoundException("Read or descrpt class stream failed:" + name, ioe);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ioe) {
						throw new ClassNotFoundException("Close class stream failed:" + name, ioe);
					}
				}
			}
		}
		return result;
	}

	public final void encZipFile(File srcZipFile, File dstZipFile) throws Exception {

		if (dstZipFile.exists()) {
			throw new IOException("File exists: " + dstZipFile);
		} else if (!dstZipFile.getParentFile().exists()) {
			dstZipFile.getParentFile().mkdirs();
		}

		ZipInputStream zis = null;
		ZipOutputStream zos = null;
		try {
			zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(srcZipFile)));
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dstZipFile)));
			for (ZipEntry ze = null; (ze = zis.getNextEntry()) != null;) {
				if (!ze.isDirectory()) {
					byte[] buf = readStreamBytes(zis);
					if (ze.getName().endsWith(".class")) {
						buf = encrptBytes(passwd, buf);
						ze = new ZipEntry(getEncryptEntryPath(ze));
					}
					zos.putNextEntry(ze);
					zos.write(buf);
					zos.closeEntry();
				} else {
					zos.putNextEntry(ze);
				}
			}
		} finally {
			if (zis != null) {
				zis.close();
			}
			if (zos != null) {
				zos.close();
			}
		}
	}

	protected static byte[] readStreamBytes(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		for (byte[] buf = new byte[BAOS_BUFF_SIZE]; (len = in.read(buf)) > 0;) {
			baos.write(buf, 0, len);
		}
		return baos.toByteArray();
	}

	protected static String getEncryptEntryPath(ZipEntry ze) {
		return new StringBuilder(128).append(ze.getName()).append(ENCRY_CLASS_SUFFIX).toString();
	}

	protected static String getEncryptClassPath(String className) {
		return new StringBuilder(128).append("/").append(className.replace('.', '/')).append(".class").append(ENCRY_CLASS_SUFFIX).toString();
	}

	protected abstract byte[] encrptBytes(String passwd, byte[] bytes) throws Exception;

	protected abstract byte[] decrptBytes(String passwd, byte[] bytes) throws Exception;

	protected static String readCryptoPasswd() {

		File file = null;
		String path = System.getProperty(CRYPTO_PASSWD_FILE);
		if (path != null) {
			file = new File(path);
		} else {
			String home = System.getProperty("user.home");
			file = new File(home, "." + CRYPTO_PASSWD_FILE);
		}
		if (!file.exists()) {
			throw new MessageException(LoaderErrno.SOURCE, LoaderErrno.CRYPTO_PASSWD_FILE_NOT_FOUND, "Crypto passwd file not found! " + file.getAbsolutePath());
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

}
