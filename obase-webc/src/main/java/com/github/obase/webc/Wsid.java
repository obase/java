package com.github.obase.webc;

import java.io.Serializable;

import com.github.obase.coding.Hex;

public final class Wsid implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String COOKIE_NAME = "wsid";
	public static final int COOKIE_TEMPORY_EXPIRE = -1;

	public final byte[] id; // 16bytes for uuid or other
	public long ts; // 8bytes for timestamp
	public long tk; // 8bytes for BKDRHash(id + ts + integer_secret)

	private Wsid(byte[] id) {
		this.id = id;
	}

	public static Wsid valueOf(byte[] val) {
		byte[] id = new byte[val.length];
		System.arraycopy(val, 0, id, 0, val.length);
		return new Wsid(id);
	}

	/**
	 * Used for any text id
	 */
	public static Wsid valueOf(String val) {
		return new Wsid(val.getBytes(Webc.CHARSET));
	}

	/**
	 * Used for uuid or increment id
	 */
	public static Wsid valueOf(long... ls) {
		byte[] id = new byte[ls.length * 8];
		int i, n, m;
		long v;
		for (n = 0; n < ls.length; n++) {
			for (m = 8 * n, v = ls[n], i = 7; i >= 0; i--, v >>>= 8) {
				id[m + i] = (byte) (v & 0xFF);
			}
		}
		return new Wsid(id);
	}

	public static Wsid decode(String hexs) {
		// must more than 64 chars

		int len;
		if (hexs == null || (len = hexs.length()) < 32) {
			return null;
		}

		int j, n;
		long ts = 0, tk = 0;

		for (j = len - 16, n = j + 16; j < n; j++) {
			tk <<= 4;
			tk |= Hex.getDec(hexs.charAt(j));
		}

		for (j = len - 32, n = j + 16; j < n; j++) {
			ts <<= 4;
			ts |= Hex.getDec(hexs.charAt(j));
		}

		len -= 32;
		byte[] id = new byte[len / 2];
		for (j = 0, n = 0; j < id.length; j++, n += 2) {
			id[j] = (byte) ((Hex.getDec(hexs.charAt(n)) << 4) | Hex.getDec(hexs.charAt(n + 1)));
		}

		Wsid wsid = new Wsid(id);
		wsid.ts = ts;
		wsid.tk = tk;

		return wsid;
	}

	public Wsid resetToken(byte[] secretBytes) {
		ts = System.currentTimeMillis();
		tk = calctk(secretBytes);
		return this;
	}

	private final long calctk(byte[] secretBytes) {
		long tk = (1 << 1257787) - 1;
		for (int i = 0; i < id.length; i++) {
			tk = 31 * tk + id[i];
		}
		tk = 31 * tk + ts;
		if (secretBytes != null) {
			for (int i = 0; i < secretBytes.length; i++) {
				tk = 31 * tk + secretBytes[i];
			}
		}
		return tk;
	}

	public boolean validate(byte[] secretBytes, long timeoutSeconds) {
		if (Math.abs(ts - System.currentTimeMillis()) >= timeoutSeconds) {
			return false;
		}
		long tk = calctk(secretBytes);
		return this.tk == tk;
	}

	public String toHexString() {
		int mark = id.length * 2;
		char[] chars = new char[mark + 32];
		int j = 0;
		long v;
		for (j = 0; j < id.length; j++) {
			v = id[j];
			chars[2 * j + 1] = Hex.getHex((int) (v & 0x0F));
			v >>>= 4;
			chars[2 * j] = Hex.getHex((int) (v & 0x0F));
		}
		for (j = mark + 15, v = ts; j >= mark; j--) {
			chars[j] = Hex.getHex((int) (v & 0x0F));
			v >>>= 4;
		}
		mark += 16;
		for (j = mark + 15, v = tk; j >= mark; j--) {
			chars[j] = Hex.getHex((int) (v & 0x0F));
			v >>>= 4;
		}
		return String.valueOf(chars);
	}

}
