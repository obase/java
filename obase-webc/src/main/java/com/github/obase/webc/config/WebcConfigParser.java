package com.github.obase.webc.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;

import com.github.obase.xml.Xmls;

public class WebcConfigParser {

	public static WebcConfig parse(Resource rs) {

		try {
			String content = readAndTrim(rs);
			if (content.length() > 0 && matches(content)) {
				return Xmls.readValue(content, WebcConfig.class);
			} else {
				return new WebcConfig();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readAndTrim(Resource rs) throws IOException {
		StringBuilder buf = new StringBuilder(1024);
		if (rs.contentLength() > 0) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(rs.getInputStream()));
				int len = 0;
				for (char[] cbuf = new char[512]; (len = reader.read(cbuf)) > 0;) {
					buf.append(cbuf, 0, len);
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		return buf.toString().trim();
	}

	private static boolean matches(String input) {
		int pos = input.indexOf(WebcConfig.ROOT_ELEMENT);
		if (pos <= 0 || pos >= input.length()) {
			return false;
		}
		char ch1 = input.charAt(pos - 1);
		char ch2 = input.charAt(pos + WebcConfig.ROOT_ELEMENT.length());
		if (ch1 != '<' && ch1 != ':') {
			return false;
		}
		if (!Character.isWhitespace(ch2) && ch2 != '>') {
			return false;
		}
		return true;

	}

}
