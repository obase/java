package com.github.obase.config;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

class RulesSAXParser extends DefaultHandler2 {

	static final String ELEM_RULES = "rules";
	static final String ELEM_RULE = "rule";

	static final String ATTR_NAME = "name";
	static final String ATTR_TYPE = "type";
	static final String ATTR_CRYPTED = "crypted";
	static final String ATTR_PASSWD = "passwd";
	static final String ATTR_REQUIRED = "required";
	static final String ATTR_DEFAULT = "default";

	Rules result;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (ELEM_RULES.equals(localName)) {
			result = new Rules(attributes.getValue(ATTR_PASSWD));
		} else if (ELEM_RULE.equals(localName)) {
			String name = attributes.getValue(ATTR_NAME);
			String type = attributes.getValue(ATTR_TYPE);
			String required = attributes.getValue(ATTR_REQUIRED);
			String default_ = attributes.getValue(ATTR_DEFAULT);
			String crptyed = attributes.getValue(ATTR_CRYPTED);
			String passwd = attributes.getValue(ATTR_PASSWD);
			if (StringUtils.isEmpty(passwd)) {
				passwd = result.passwd;
			}
			result.rules.add(new Rule(name, type, required, default_, crptyed, passwd));
		}
	}

	static WeakReference<SAXParser> Ref = null;

	static synchronized SAXParser getSAXParser() throws ParserConfigurationException, SAXException {
		if (Ref == null || Ref.get() == null) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			SAXParser parser = factory.newSAXParser();

			Ref = new WeakReference<SAXParser>(parser);
		}
		return Ref.get();
	}

	public static Rules parse(Resource rs) throws Exception {
		if (rs == null || !rs.exists()) {
			return null;
		}
		SAXParser parser = getSAXParser();
		RulesSAXParser handler = new RulesSAXParser();
		InputStream in = null;
		try {
			in = rs.getInputStream();
			parser.parse(new BufferedInputStream(in), handler);
			return handler.result;
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

}
