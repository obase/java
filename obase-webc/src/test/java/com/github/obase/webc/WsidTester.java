package com.github.obase.webc;

import java.io.UnsupportedEncodingException;

import com.github.obase.json.Jsons;

public class WsidTester {

	public static void main(String[] args) throws UnsupportedEncodingException {

		Wsid wsid = Wsid.valueOf(1234567890123456L).resetToken(1234567);
		System.out.println(Jsons.writeAsString(wsid));
		String tk = Wsid.encode(wsid);
		System.out.println(tk);
		wsid = Wsid.decode(tk);
		System.out.println(Jsons.writeAsString(wsid));
	}

}
