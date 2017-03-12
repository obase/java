package com.obase.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.github.obase.kit.ClassKit;

public abstract class EncryClassLoader extends ClassLoader {

	public static final String ENCRY_CLASS_SUFFIX = "____";
	public static int BAOS_BUFF_SIZE = 4092;

	protected EncryClassLoader() {
		this(null);
	}

	protected EncryClassLoader(ClassLoader parent) {
		super(parent != null ? parent : defaultParentClassLoader());
	}

	@Override
	protected final Class<?> findClass(String name) throws ClassNotFoundException {

		Class<?> result = null;
		try {
			result = super.findClass(name);
		} catch (ClassNotFoundException e) {
			InputStream in = null;
			try {
				in = ClassKit.getResourceAsStream(ClassKit.getClassPathFromClassName(getEncryptClassName(name)));
				if (in != null) {
					byte[] bytes = decrptBytes(readStreamBytes(in));
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
						buf = encrptBytes(buf);
						ze = new ZipEntry(getEncryptEntryName(ze));
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

	private static byte[] readStreamBytes(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		for (byte[] buf = new byte[BAOS_BUFF_SIZE]; (len = in.read(buf)) > 0;) {
			baos.write(buf, 0, len);
		}
		return baos.toByteArray();
	}

	private static ClassLoader defaultParentClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = EncryClassLoader.class.getClassLoader();
		}
		return loader;
	}

	private static String getEncryptEntryName(ZipEntry ze) {
		StringBuilder sb = new StringBuilder(ze.getName());
		int pos = sb.lastIndexOf(".class");
		sb.insert(pos, ENCRY_CLASS_SUFFIX);
		return sb.toString();
	}

	private static String getEncryptClassName(String className) {
		return className + ENCRY_CLASS_SUFFIX;
	}

	/**
	 * encrypt implementation by subclass
	 */
	protected abstract byte[] encrptBytes(byte[] bytes) throws Exception;

	/**
	 * decrypt implementation by subclass
	 */
	protected abstract byte[] decrptBytes(byte[] bytes) throws Exception;
}
