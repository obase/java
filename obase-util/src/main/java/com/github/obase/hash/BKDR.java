package com.github.obase.hash;

public final class BKDR {

	private static final int DEFAULT_SEED = 274372069; // 29bit

	private BKDR() {
	}

	/********** Hash32 ***********/
	public static class Hash32 {

		final int seed;
		int hash;

		public Hash32(int base, int seed) {
			this.hash = base;
			this.seed = seed;
		}

		public int value() {
			return hash;
		}

		public Hash32 hash32(boolean val) {
			hash = seed * hash + (val ? 1 : 0);
			return this;
		}

		public Hash32 hash32(byte val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash32 hash32(char val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash32 hash32(short val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash32 hash32(int val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash32 hash32(long val) {
			hash = seed * hash + (int) (val ^ (val >>> 32));
			return this;
		}

		public Hash32 hash32(float val) {
			hash = seed * hash + Float.floatToIntBits(val);
			return this;
		}

		public Hash32 hash32(double val) {
			long v = Double.doubleToLongBits(val);
			hash = seed * hash + (int) (v ^ (v >>> 32));
			return this;
		}

		public Hash32 hash32(String val) {
			for (int i = 0, n = val.length(); i < n; i++) {
				hash32(val.charAt(i));
			}
			return this;
		}

		public Hash32 hash32(boolean[] val) {
			for (boolean v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(byte[] val) {
			for (byte v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(char[] val) {
			for (char v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(short[] val) {
			for (short v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(int[] val) {
			for (int v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(long[] val) {
			for (long v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(float[] val) {
			for (float v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(double[] val) {
			for (double v : val) {
				hash32(v);
			}
			return this;
		}

		public Hash32 hash32(String[] val) {
			for (String v : val) {
				hash32(v);
			}
			return this;
		}

	}

	/********** Hash64 ***********/
	public static class Hash64 {

		final long seed;
		long hash;

		public Hash64(long base, long seed) {
			this.hash = base;
			this.seed = seed;
		}

		public long value() {
			return hash;
		}

		public Hash64 hash64(boolean val) {
			hash = seed * hash + (val ? 1 : 0);
			return this;
		}

		public Hash64 hash64(byte val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash64 hash64(char val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash64 hash64(short val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash64 hash64(int val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash64 hash64(long val) {
			hash = seed * hash + val;
			return this;
		}

		public Hash64 hash64(float val) {
			hash = seed * hash + Float.floatToIntBits(val);
			return this;
		}

		public Hash64 hash64(double val) {
			hash = seed * hash + Double.doubleToLongBits(val);
			return this;
		}

		public Hash64 hash64(String val) {
			for (int i = 0, n = val.length(); i < n; i++) {
				hash64(val.charAt(i));
			}
			return this;
		}

		public Hash64 hash64(boolean[] val) {
			for (boolean v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(byte[] val) {
			for (byte v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(char[] val) {
			for (char v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(short[] val) {
			for (short v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(int[] val) {
			for (int v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(long[] val) {
			for (long v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(float[] val) {
			for (float v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(double[] val) {
			for (double v : val) {
				hash64(v);
			}
			return this;
		}

		public Hash64 hash64(String[] val) {
			for (String v : val) {
				hash64(v);
			}
			return this;
		}

	}

	public static int hash32(String val) {
		return hash32(0, DEFAULT_SEED, val);
	}

	public static int hash32(int seed, String val) {
		return hash32(0, seed, val);
	}

	public static int hash32(int base, int seed, String val) {
		for (int i = 0, n = val.length(); i < n; i++) {
			base = seed * base + val.charAt(i);
		}
		return base;
	}

	public static int hash32(byte[] val) {
		return hash32(0, DEFAULT_SEED, val);
	}

	public static int hash32(int seed, byte[] val) {
		return hash32(0, seed, val);
	}

	public static int hash32(int base, int seed, byte[] val) {
		for (int i = 0, n = val.length; i < n; i++) {
			base = seed * base + val[i];
		}
		return base;
	}

	public static long hash64(String val) {
		return hash64(0, DEFAULT_SEED, val);
	}

	public static long hash64(long seed, String val) {
		return hash64(0, seed, val);
	}

	public static long hash64(long base, long seed, String val) {
		for (int i = 0, n = val.length(); i < n; i++) {
			base = seed * base + val.charAt(i);
		}
		return base;
	}

	public static long hash64(byte[] val) {
		return hash64(0, DEFAULT_SEED, val);
	}

	public static long hash64(long seed, byte[] val) {
		return hash64(0, seed, val);
	}

	public static long hash64(long base, long seed, byte[] val) {
		for (int i = 0, n = val.length; i < n; i++) {
			base = seed * base + val[i];
		}
		return base;
	}

	public static Hash32 hash32() {
		return new Hash32(0, DEFAULT_SEED);
	}

	public static Hash32 hash32(int seed) {
		return new Hash32(0, seed);
	}

	public static Hash32 hash32(int base, int seed) {
		return new Hash32(base, seed);
	}

	public static Hash64 hash64() {
		return new Hash64(0, DEFAULT_SEED);
	}

	public static Hash64 hash64(long seed) {
		return new Hash64(0, seed);
	}

	public static Hash64 hash64(long base, long seed) {
		return new Hash64(base, seed);
	}

}
