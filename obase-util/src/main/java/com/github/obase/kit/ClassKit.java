package com.github.obase.kit;

import com.github.obase.loader.DelegatedClassLoader;

public class ClassKit {

	private ClassKit() {
	}

	public static final DelegatedClassLoader Loader = new DelegatedClassLoader(ClassKit.class.getClassLoader());
	
	
}
