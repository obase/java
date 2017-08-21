package com.github.obase.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class ClassKit {

	private ClassKit() {
	}

	public static final DelegateClassLoader ContextClassLoader = new DelegateClassLoader(contextClassLoader());

	public static class DelegateClassLoader extends ClassLoader {

		public DelegateClassLoader(ClassLoader delegate) {
			super(delegate);
		}

		public Class<?> defineClass(String name, byte[] data) {
			return super.defineClass(name, data, 0, data.length);
		}

	}

	public static Class<?> defineClass(String name, byte[] data, ClassLoader loader) {
		return new DelegateClassLoader(loader).defineClass(name, data);
	}

	public static Class<?> defineClass(String name, byte[] data) {
		return ContextClassLoader.defineClass(name, data);
	}

	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		return ContextClassLoader.loadClass(name);
	}

	public static Class<?> forName(String name) throws ClassNotFoundException {
		return ContextClassLoader.loadClass(name);
	}

	public static ClassLoader contextClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassKit.class.getClassLoader();
		}
		return loader;
	}

	public static String getResourceAsString(String classpath) throws IOException {
		InputStream in = null;
		try {
			// FIXBUG: not const ContextClassLoader
			in = ClassKit.class.getResourceAsStream(classpath);
			if (in != null) {
				Reader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder(1024);
				int len = 0;
				for (char[] buff = new char[1024]; (len = reader.read(buff)) > 0;) {
					sb.append(buff, 0, len);
				}
				return sb.toString();
			}
			return null;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static InputStream getResourceAsStream(String classpath) {
		// FIXBUG: not const ContextClassLoader
		return ClassKit.class.getResourceAsStream(classpath);
	}

	public static String getClassPathFromClassName(String className) {
		return new StringBuilder(128).append('/').append(className.replace('.', '/')).append(".class").toString();
	}

	public static String getClassPathFromInternalName(String internalName) {
		return new StringBuilder(128).append('/').append(internalName).append(".class").toString();
	}

	public static String getClassNameFromInternalName(String internalName) {
		return internalName.replace('/', '.');
	}

	public static String getInternalNameFromClassName(String className) {
		return className.replace('.', '/');
	}
}
