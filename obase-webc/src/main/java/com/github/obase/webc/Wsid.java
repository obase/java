package com.github.obase.webc;

import java.io.Serializable;
import java.util.Arrays;

import com.github.obase.coding.Base64.Decoder;
import com.github.obase.coding.Base64.Encoder;

/**
 * 该类的fromHexs与toHexs是一组对称方法!必须保证二者的对称性.
 */
public final class Wsid implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String COOKIE_NAME = "wsid";
	public static final int COOKIE_TEMPORY_EXPIRE = -1;
	public static final byte KEY_SEP = (byte) '\001';

	public final byte[] id; // 16bytes for uuid or other
	public long ts; // 8bytes for timestamp
	public int tk; // 4bytes for BKDRHash(base, id + ts)

	private Wsid(byte[] id) {
		this.id = id;
	}

	public static byte[] keysPattern(String uid) {
		byte[] data = uid.getBytes();
		data = Arrays.copyOf(data, data.length + 2);
		data[data.length - 1] = '*';
		data[data.length - 2] = KEY_SEP;
		return data;
	}

	public static byte[] keysPattern(long uid) {
		byte[] data = new byte[8 + 2];
		for (int i = 7; i >= 0; i--) {
			data[i] = (byte) (uid & 0xFF);
			uid >>>= 8;
		}
		data[data.length - 1] = '*';
		data[data.length - 2] = KEY_SEP;
		return data;
	}

	/**
	 * Used for any text id
	 */
	public static Wsid valueOf(String uid) {

		byte[] data = uid.getBytes();
		long sid = System.nanoTime(); // using nanotime as session id

		int len = data.length;
		data = Arrays.copyOf(data, len + 8);

		data[len] = KEY_SEP;
		for (int i = len + 7; i > len; i--) {
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

		for (int i = 7; i > 0; i--) {
			data[i] = (byte) (uid & 0xFF);
			sid >>>= 8;
		}

		data[8] = KEY_SEP;
		for (int i = 15; i > 8; i--) {
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

	public static Wsid decode(String hexs) {

		byte[] data = Decoder.RFC4648_URLSAFE.decode(hexs);
		if (data.length < 12) {
			return null;
		}
		int idx = data.length - 12;
		byte[] id = new byte[idx];
		System.arraycopy(data, 0, id, 0, idx);
		Wsid wsid = new Wsid(id);
		for (int n = idx + 8; idx < n; idx++) {
			wsid.ts = (wsid.ts << 8) | (data[idx] & 0xFF);
		}
		for (int n = idx + 4; idx < n; idx++) {
			wsid.tk = (wsid.tk << 8) | (data[idx] & 0xFF);
		}
		return wsid;
	}

	public static String encode(Wsid wsid) {

		byte[] data = new byte[wsid.id.length + 12];
		int idx = data.length - 1;
		for (int n = idx - 4; idx > n; idx--) {
			data[idx] = (byte) (wsid.tk & 0xFF);
			wsid.tk >>>= 8;
		}
		for (int n = idx - 8; idx > n; idx--) {
			data[idx] = (byte) (wsid.ts & 0xFF);
			wsid.ts >>>= 8;
		}
		System.arraycopy(wsid.id, 0, data, 0, idx + 1);
		return Encoder.RFC4648_URLSAFE.encodeToString(data);
	}

}
