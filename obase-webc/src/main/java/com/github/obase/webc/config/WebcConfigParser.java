package com.github.obase.webc.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.github.obase.kit.SAXKit;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.Webc;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.webc.config.WebcConfig.Props;

public class WebcConfigParser extends DefaultHandler2 {

	SpelExpressionParser parser;
	WebcConfig target;
	FilterInitParam param;

	final StringBuilder content = new StringBuilder(512);

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Props.webc.equals(localName)) {
			target = new WebcConfig();
		} else if (Props.withoutApplicationContext.equals(localName)) {

		} else if (Props.withoutServletContext.equals(localName)) {

		} else if (Props.withoutServiceContext.equals(localName)) {

		} else if (Props.contextConfigLocation.equals(localName)) {

		} else if (Props.servlet.equals(localName)) {
			param = new FilterInitParam();
		} else if (Props.service.equals(localName)) {
			param = new FilterInitParam();
		} else if (Props.namespace.equals(localName)) {

		} else if (Props.asyncListener.equals(localName)) {

		} else if (Props.timeoutSecond.equals(localName)) {

		} else if (Props.sendError.equals(localName)) {

		} else if (Props.controlProcessor.equals(localName)) {

		} else if (Props.controlPrefix.equals(localName)) {

		} else if (Props.controlSuffix.equals(localName)) {

		} else if (Props.wsidTokenBase.equals(localName)) {

		} else if (Props.defaultAuthType.equals(localName)) {

		} else if (Props.refererDomain.equals(localName)) {

		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Props.webc.equals(localName)) {

		} else if (Props.withoutApplicationContext.equals(localName)) {
			target.withoutApplicationContext = cleanContentAsBoolean(false);
		} else if (Props.withoutServletContext.equals(localName)) {
			target.withoutServletContext = cleanContentAsBoolean(false);
		} else if (Props.withoutServiceContext.equals(localName)) {
			target.withoutServiceContext = cleanContentAsBoolean(false);
		} else if (Props.contextConfigLocation.equals(localName)) {
			if (param == null) {
				target.contextConfigLocation = cleanContentAsString(null);
			} else {
				param.contextConfigLocation = cleanContentAsString(null);
			}
		} else if (Props.servlet.equals(localName)) {
			target.servlets.add(param);
			param = null;
		} else if (Props.service.equals(localName)) {
			target.services.add(param);
			param = null;
		} else if (Props.namespace.equals(localName)) {
			param.namespace = cleanContentAsString(null);
		} else if (Props.asyncListener.equals(localName)) {
			param.asyncListener = cleanContentAsString(null);
		} else if (Props.timeoutSecond.equals(localName)) {
			param.timeoutSecond = cleanContentAsInt(Webc.DEFAULT_TIMEOUT_SECOND);
		} else if (Props.sendError.equals(localName)) {
			param.sendError = cleanContentAsBoolean(false);
		} else if (Props.controlProcessor.equals(localName)) {
			param.controlProcessor = cleanContentAsString(null);
		} else if (Props.controlPrefix.equals(localName)) {
			param.controlPrefix = cleanContentAsString(null);
		} else if (Props.controlSuffix.equals(localName)) {
			param.controlSuffix = cleanContentAsString(null);
		} else if (Props.wsidTokenBase.equals(localName)) {
			param.wsidTokenBase = cleanContentAsInt(Webc.DEFAULT_WSID_TOKEN_BASE);
		} else if (Props.defaultAuthType.equals(localName)) {
			param.defaultAuthType = Webc.DEFAULT_AUTH_TYPE;
		} else if (Props.refererDomain.equals(localName)) {
			param.refererDomain = cleanContentAsString(null);
		}
		content.setLength(0);
	}

	private int cleanContentAsInt(int defval) {
		String value = cleanContentAsString(null);
		if (StringKit.isEmpty(value)) {
			return defval;
		}
		return Integer.parseInt(value);
	}

	private boolean cleanContentAsBoolean(boolean defval) {
		String value = cleanContentAsString(null);
		if (StringKit.isEmpty(value)) {
			return defval;
		}
		return Boolean.parseBoolean(value);
	}

	private String cleanContentAsString(String defval) {
		String value = content.toString().trim();
		content.setLength(0);
		if (StringKit.isEmpty(value)) {
			return defval;
		}

		if (value.startsWith("#{") || value.endsWith("}")) {// Spring EL expression
			if (parser == null) {
				parser = new SpelExpressionParser();
			}
			Expression expr = parser.parseExpression(value.substring(2, value.length() - 1));
			Object obj = expr.getValue();
			return obj == null ? null : obj.toString();
		} else {
			return value;
		}
	}

	public static WebcConfig parse(Resource rs) {

		try {
			String content = readAndTrim(rs);
			if (content.length() > 0 && matches(content)) {
				WebcConfigParser handler = new WebcConfigParser();
				SAXKit.parse(new InputSource(new StringReader(content)), handler);
				return handler.target;
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
		int pos = input.indexOf(Props.webc);
		if (pos <= 0 || pos >= input.length()) {
			return false;
		}
		char ch1 = input.charAt(pos - 1);
		char ch2 = input.charAt(pos + Props.webc.length());
		if (ch1 != '<' && ch1 != ':') {
			return false;
		}
		if (!Character.isWhitespace(ch2) && ch2 != '>') {
			return false;
		}
		return true;

	}

}
