package com.github.obase.kit;

public final class ClassKit {

	private ClassKit() {
	}

	private static final DelegateClassLoader ContextClassLoader = new DelegateClassLoader(contextClassLoader());

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
}
