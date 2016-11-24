package com.github.obase.loader;

/**
 * Delegate class loader for other class
 *
 */
public class DelegatedClassLoader extends ClassLoader {

	public DelegatedClassLoader(ClassLoader delegate) {
		super(delegate);
	}

	public Class<?> defineClass(String name, byte[] data) {
		return super.defineClass(name, data, 0, data.length);
	}

}
