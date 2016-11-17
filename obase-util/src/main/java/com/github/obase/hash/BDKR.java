package com.github.obase.hash;

public class BDKR {

	private BDKR() {
	}

	private static final int DEFAULT_SEED = 104729;

	public static int hash32(String val) {
		return hash32(val, DEFAULT_SEED);
	}

	public static int hash32(String val, int seed) {
		int hash = seed;
		for (int i = 0, n = val.length(); i < n; i++) {
			hash = seed * hash + val.charAt(i);
		}
		return hash;
	}

	public static long hash64(String val, int seed) {
		long hash = seed;
		for (int i = 0, n = val.length(); i < n; i++) {
			hash = seed * hash + val.charAt(i);
		}
		return hash;
	}

}
