package com.github.obase.kit;

public final class ClassKit {

	private ClassKit() {
	}

	public static final DelegateClassLoader DefinedClassLoader = new DelegateClassLoader(ClassKit.class.getClassLoader());

	public static class DelegateClassLoader extends ClassLoader {

		public DelegateClassLoader(ClassLoader delegate) {
			super(delegate);
		}

		public Class<?> defineClass(String name, byte[] data) {
			return super.defineClass(name, data, 0, data.length);
		}

	}

}
