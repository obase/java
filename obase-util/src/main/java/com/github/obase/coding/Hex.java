package com.github.obase.coding;

/**
 * Tables useful when converting byte arrays to and from strings of hexadecimal digits. Code from Ajp11, from Apache's JServ.
 *
 * @author Craig R. McClanahan
 */

public final class Hex {

	/**
	 * Table for HEX to DEC byte translation.
	 */
	 private static final byte[] DEC = {
		        00, 01, 02, 03, 04, 05, 06, 07,  8,  9, -1, -1, -1, -1, -1, -1,
		        -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		        -1, 10, 11, 12, 13, 14, 15,
		    };
	/**
	 * Table for byte to hex string translation.
	 */
	private static final char[] HEX = "0123456789abcdef".toCharArray();

	public static byte getDec(char ch) {
		return DEC[ch - '0'];
	}

	public static char getHex(int index) {
		return HEX[index];
	}

	public static String encode(byte[] bytes) {
		if (null == bytes) {
			return null;
		}

		char[] data = new char[bytes.length << 1];

		for (int i = 0; i < bytes.length; ++i) {
			data[i * 2] = HEX[(bytes[i] >>> 4) & 0x0f];
			data[i * 2 + 1] = HEX[bytes[i] & 0x0f];
		}

		return String.valueOf(data);
	}

	public static byte[] decode(String vals) {
		if (vals == null) {
			return null;
		}
		int len = vals.length();

		byte[] data = new byte[len / 2];
		for (int i = 0, j = 0; i < data.length; i++, j += 2) {
			data[i] = (byte) ((getDec(vals.charAt(j)) << 4) | getDec(vals.charAt(j + 1)));
		}
		return data;
	}

}
