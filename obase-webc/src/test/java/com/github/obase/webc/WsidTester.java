package com.github.obase.webc;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.github.obase.json.Jsons;

public class WsidTester {

	public static void main(String[] args) throws UnsupportedEncodingException {

		Wsid wsid = Wsid.valueOf(UUID.randomUUID().toString().replace("-", "")).resetToken(1234567);
		System.out.println(Jsons.writeAsString(wsid));
		String tk = Wsid.encode(wsid);
		System.out.println(tk);
		wsid=Wsid.decode(tk);
		System.out.println(Jsons.writeAsString(wsid));
	}

	private static String encode(Wsid wsid) {
		return new StringBuilder(wsid.id.length() + 36).append("").append('-').append(Long.toHexString(wsid.ts)).append('-').append(Integer.toHexString(wsid.tk)).toString();
	}

	private static Wsid decode(String tk) {
		int pos1 = tk.indexOf('-');
		int pos2 = tk.indexOf('-', pos1 + 1);
		if (pos2 == -1) {
			return null;
		}
		Wsid wsid = new Wsid(null);
		wsid.ts = Long.parseLong(tk.substring(pos1 + 1, pos2), 16);
		wsid.tk = (int) Long.parseLong(tk.substring(pos2 + 1), 16);
		return wsid;
	}

}
