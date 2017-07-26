package com.github.obase.webc;

import java.io.Serializable;

/**
 * 该类的fromHexs与toHexs是一组对称方法!必须保证二者的对称性.
 */
public final class Wsid implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String COOKIE_NAME = "wsid";
	public static final int COOKIE_TEMPORY_EXPIRE = -1;
	public static final char COOKIE_MARKER = '-';
	public static final String COOKIE_PATH = "/";

	public final String id; // 16bytes for uuid or other
	public long ts; // 8bytes for timestamp
	public int tk; // 4bytes for BKDRHash(base, id + ts)

	public Wsid(String id) {
		this.id = id;
	}

	public static String keysPattern(String uid) {
		return new StringBuilder(uid.length() + 8).append(COOKIE_MARKER).append(uid).append(COOKIE_MARKER).append('*').toString();
	}

	public static String keysPattern(long uid) {
		return new StringBuilder(32).append(COOKIE_MARKER).append(uid).append(COOKIE_MARKER).append('*').toString();
	}

	/**
	 * Used for any text id
	 */
	public static Wsid valueOf(String uid) {
		return new Wsid(new StringBuilder(uid.length() + 32).append(COOKIE_MARKER).append(uid).append(COOKIE_MARKER).append(Long.toHexString(System.nanoTime())).toString());
	}

	/**
	 * Used for uuid or increment id
	 */
	public static Wsid valueOf(long uid) {
		return new Wsid(new StringBuilder(36).append(uid).append(COOKIE_MARKER).append(Long.toHexString(System.nanoTime())).toString());
	}

	public Wsid resetToken(long base) {
		ts = System.currentTimeMillis();
		tk = signature(id, ts, base);
		return this;
	}

	public boolean validate(int base, long timeoutMillis) {
		long diff = System.currentTimeMillis() - ts;
		if ((diff > 0 && diff >= timeoutMillis) || (diff < 0 && -2 * diff > timeoutMillis)) {
			return false;
		}
		int tk = signature(id, ts, base);
		return this.tk == tk;
	}

	private static int signature(String sid, long nonce, long base) {
		int hash = 31 * (int) (base ^ (base >>> 32));
		for (int i = 0, n = sid.length(); i < n; i++) {
			hash = 31 * hash + sid.charAt(i);
		}
		hash = 31 * hash + (int) (nonce ^ (nonce >>> 32));
		return hash;
	}

	public static Wsid decode(String hexs) {
		int pos2 = hexs.lastIndexOf(COOKIE_MARKER);
		int pos1 = hexs.lastIndexOf(COOKIE_MARKER, pos2 - 1);
		if (pos1 == -1) {
			return null;
		}
		Wsid wsid = new Wsid(hexs.substring(0, pos1));
		wsid.ts = Long.parseLong(hexs.substring(pos1 + 1, pos2), 16);
		wsid.tk = (int) Long.parseLong(hexs.substring(pos2 + 1), 16);
		return wsid;
	}

	public static String encode(Wsid wsid) {
		return new StringBuilder(wsid.id.length() + 44).append(wsid.id).append(COOKIE_MARKER).append(Long.toHexString(wsid.ts)).append(COOKIE_MARKER).append(Integer.toHexString(wsid.tk)).toString();
	}

}
