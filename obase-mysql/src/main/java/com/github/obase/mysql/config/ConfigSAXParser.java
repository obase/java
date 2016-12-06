package com.github.obase.mysql.config;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigSAXParser extends DefaultHandler {

	static final String ELEM_MYSQL = "obase-mysql";
	static final String ELEM_TABLE = "table-class";
	static final String ELEM_META = "meta-class";
	static final String ELEM_SQL = "statement";

	static final String ATTR_NAMESPACE = "namespace";
	static final String ATTR_ID = "id";

	ConfigMetaInfo config;
	boolean skip;
	String id;
	final StringBuilder content = new StringBuilder(512);

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (ELEM_MYSQL.equals(localName)) {
			config = new ConfigMetaInfo(attributes.getValue(ATTR_NAMESPACE));
			skip = true;
		} else {
			id = attributes.getValue(ATTR_ID);
			skip = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (ELEM_TABLE.equals(localName)) {
			config.tables.add(content.toString().trim());
			content.setLength(0);
		} else if (ELEM_META.equals(localName)) {
			config.metas.add(content.toString().trim());
			content.setLength(0);
		} else if (ELEM_SQL.equals(localName)) {
			config.sqls.put(id, content.toString().trim());
			content.setLength(0);
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

	public static ConfigMetaInfo parse(Resource rs) throws Exception {
		SAXParser parser = getSAXParser();
		ConfigSAXParser handler = new ConfigSAXParser();
		InputStream in = null;
		try {
			in = rs.getInputStream();
			parser.parse(new BufferedInputStream(in), handler);
			return handler.config;
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}
}
