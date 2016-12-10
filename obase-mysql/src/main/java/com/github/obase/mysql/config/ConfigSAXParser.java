package com.github.obase.mysql.config;

import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.obase.kit.SAXKit;

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

	public static ConfigMetaInfo parse(Resource rs) throws Exception {
		ConfigSAXParser handler = new ConfigSAXParser();
		if (SAXKit.parse(rs.getInputStream(), handler)) {
			return handler.config;
		}
		return null;
	}
}
