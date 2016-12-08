package com.github.obase.webc;

import java.io.Serializable;
import java.util.Arrays;

import com.github.obase.coding.Hex;

/**
 * 该类的fromHexs与toHexs是一组对称方法!必须保证二者的对称性.
 */
public final class Wsid implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String COOKIE_NAME = "wsid";
	public static final int COOKIE_TEMPORY_EXPIRE = -1;

	public final byte[] id; // 16bytes for uuid or other
	public long ts; // 8bytes for timestamp
	public int tk; // 4bytes for BKDRHash(base, id + ts)

	private Wsid(byte[] id) {
		this.id = id;
	}

	/**
	 * Used for any text id
	 */
	public static Wsid valueOf(String uid) {

		byte[] data = uid.getBytes();
		long sid = System.nanoTime(); // using nanotime as session id

		int len = data.length;
		data = Arrays.copyOf(data, len + 8);

		for (int i = len + 7; i >= len; i--) {
			data[i] = (byte) (sid & 0xFF);
			sid >>>= 8;
		}

		return new Wsid(data);
	}

	/**
	 * Used for uuid or increment id
	 */
	public static Wsid valueOf(long uid) {

		byte[] data = new byte[16];
		long sid = System.nanoTime(); // using nanotime as session id

		for (int i = 7; i >= 0; i--) {
			data[i] = (byte) (uid & 0xFF);
			sid >>>= 8;
		}

		for (int i = 15; i >= 8; i--) {
			data[i] = (byte) (sid & 0xFF);
			sid >>>= 8;
		}

		return new Wsid(data);
	}

	public Wsid resetToken(int base) {
		ts = System.currentTimeMillis();
		tk = BKDRHash(base);
		return this;
	}

	private final int BKDRHash(int base) {
		int tk = base;
		for (int i = 0; i < id.length; i++) {
			tk = 31 * tk + id[i];
		}
		tk = 31 * tk + (int) ((ts >>> 32) ^ ts);
		return tk;
	}

	public boolean validate(int base, long timeoutMillis) {
		if (Math.abs(System.currentTimeMillis() - ts) > timeoutMillis) {
			return false;
		}
		long tk = BKDRHash(base);
		return this.tk == tk;
	}

	public static Wsid fromHexs(String hexs) {
		// must more than 64 chars

		int len;
		if (hexs == null || (len = hexs.length()) < 24) {
			return null;
		}

		int j, n;
		long ts = 0;
		int tk = 0;

		for (j = len - 8, n = j + 8; j < n; j++) {
			tk <<= 4;
			tk |= Hex.getDec(hexs.charAt(j));
		}

<<<<<<< HEAD
		for (j = len - 24, n = j + 16; j < n; j++) {
=======
		for (j = len - 24, n = j + 8; j < n; j++) {
>>>>>>> branch 'master' of git@github.com:obase/java.git
			ts <<= 4;
			ts |= Hex.getDec(hexs.charAt(j));
		}

		len -= 24;
		byte[] id = new byte[len / 2];
		for (j = 0, n = 0; j < id.length; j++, n += 2) {
			id[j] = (byte) ((Hex.getDec(hexs.charAt(n)) << 4) | Hex.getDec(hexs.charAt(n + 1)));
		}

		Wsid wsid = new Wsid(id);
		wsid.ts = ts;
		wsid.tk = tk;

		return wsid;
	}

<<<<<<< HEAD
	public String toHexs() {
=======
	public Wsid resetToken(int base) {
		ts = System.currentTimeMillis();
		tk = BKDRHash(base);
		return this;
	}

	private final int BKDRHash(int base) {
		int tk = base;
		for (int i = 0; i < id.length; i++) {
			tk = 31 * tk + id[i];
		}
		tk = 31 * tk + (int) ((ts >>> 32) ^ ts);
		return tk;
	}

	public boolean validate(int base, long timeoutMillis) {
		if (Math.abs(System.currentTimeMillis() - ts) > timeoutMillis) {
			return false;
		}
		long tk = BKDRHash(base);
		return this.tk == tk;
	}

	public String toHexString() {
>>>>>>> branch 'master' of git@github.com:obase/java.git
		int mark = id.length * 2;
		char[] chars = new char[mark + 24];
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
		for (j = mark + 7, v = tk; j >= mark; j--) {
			chars[j] = Hex.getHex((int) (v & 0x0F));
			v >>>= 4;
		}
		return String.valueOf(chars);
	}

}
