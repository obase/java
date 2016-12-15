package com.github.obase.hash;

public class BKDR {

	private BKDR() {
	}

	private static final int DEFAULT_SEED = 274372069; // 29bit

	public static int hash32(String... val) {
		return hash32(DEFAULT_SEED, val);
	}

	public static long hash64(String... val) {
		return hash64(DEFAULT_SEED, val);
	}

	public static int hash32(int seed, String... vals) {
		int hash = seed;
		for (String val : vals) {
			for (int i = 0, n = val.length(); i < n; i++) {
				hash = seed * hash + val.charAt(i);
			}
		}
		return hash;
	}

	public static long hash64(long seed, String... vals) {
		long hash = seed;
		for (String val : vals) {
			for (int i = 0, n = val.length(); i < n; i++) {
				hash = seed * hash + val.charAt(i);
			}
		}
		return hash;
	}

	public static int hash32(byte[]... val) {
		return hash32(DEFAULT_SEED, val);
	}

	public static long hash64(byte[]... val) {
		return hash64(DEFAULT_SEED, val);
	}

	public static int hash32(int seed, byte[]... vals) {
		int hash = seed;
		for (byte[] val : vals) {
			for (int i = 0; i < val.length; i++) {
				hash = seed * hash + val[i];
			}
		}
		return hash;
	}

	public static long hash64(long seed, byte[]... vals) {
		long hash = seed;
		for (byte[] val : vals) {
			for (int i = 0; i < val.length; i++) {
				hash = seed * hash + val[i];
			}
		}
		return hash;
	}

}
