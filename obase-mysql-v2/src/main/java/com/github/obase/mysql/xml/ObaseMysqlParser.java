package com.github.obase.mysql.xml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.github.obase.kit.StringKit;
import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.stmt.AND;
import com.github.obase.mysql.stmt.Generic;
import com.github.obase.mysql.stmt.OR;
import com.github.obase.mysql.stmt.Statement;
import com.github.obase.mysql.stmt.Static;
import com.github.obase.mysql.stmt.WHERE;
import com.github.obase.mysql.syntax.Sql;
import com.github.obase.mysql.syntax.SqlDqlKit;
import com.github.obase.mysql.syntax.SqlKit;

public final class ObaseMysqlParser {

	final DocumentBuilderFactory factory;

	public ObaseMysqlParser() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		factory.setCoalescing(true); // 必需! 将CDATA转为TEXT.
	}

	public ObaseMysqlObject parse(Resource rs) throws Exception {

		InputStream is = null;
		try {
			is = rs.getInputStream();
			if (is == null) {
				return null;
			}
			ObaseMysqlObject obj = new ObaseMysqlObject();

			Element root = factory.newDocumentBuilder().parse(new BufferedInputStream(is)).getDocumentElement();
			obj.namespace = root.getAttribute(ATTR_NAMESPACE);

			for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String tag = node.getNodeName();
				if (ELEM_TABLE.equals(tag)) {
					parseTable(obj, (Element) node);
				} else if (ELEM_META.equals(tag)) {
					parseTable(obj, (Element) node);
				} else if (ELEM_STMT.equals(tag)) {
					parseStatement(obj, (Element) node);
				}
			}

			return obj;
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}

	void parseTable(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		obj.tableClassList.add(Class.forName(className));
	}

	void parseMeta(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		obj.metaClassList.add(Class.forName(className));
	}

	void parseStatement(ObaseMysqlObject obj, Element root) {
		String id = root.getAttribute(ATTR_ID);
		String nop = root.getAttribute(ATTR_NOP);
		List<Fragment> fragments = parseChildrenFragment(root);
		if (!fragments.isEmpty()) {
			obj.statementList.add(new Statement(id, "true".equalsIgnoreCase(nop), fragments));
		}
	}

	private List<Fragment> parseChildrenFragment(Element root) {
		List<Fragment> fragments = new LinkedList<Fragment>();

		for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
			short nt = node.getNodeType();
			Fragment f = null;
			if (nt == Node.TEXT_NODE) {
				f = parseStatic(node);
			} else if (nt == Node.ELEMENT_NODE) {
				String tag = node.getNodeName();
				if (ELEM_WHERE.equals(tag)) {
					f = parseWHERE((Element) node);
				} else if (ELEM_AND.equals(tag)) {
					f = parseAND((Element) node);
				} else if (ELEM_OR.equals(tag)) {
					f = parseOR((Element) node);
				} else if (ELEM_X.equals(tag)) {
					f = parseGeneric((Element) node);
				}
			}
			if (f != null) {
				fragments.add(f);
			}
		}
		return fragments;
	}

	private WHERE parseWHERE(Element root) {
		String s = root.getAttribute(ATTR_SEP);
		List<Fragment> fragments = parseChildrenFragment(root);
		if (!fragments.isEmpty()) {
			return new WHERE(s, fragments);
		}
		return null;
	}

	private AND parseAND(Element root) {
		String s = root.getAttribute(ATTR_SEP);
		List<Fragment> fragments = parseChildrenFragment(root);
		if (!fragments.isEmpty()) {
			return new AND(s, fragments);
		}
		return null;
	}

	private OR parseOR(Element root) {
		String s = root.getAttribute(ATTR_SEP);
		List<Fragment> fragments = parseChildrenFragment(root);
		if (!fragments.isEmpty()) {
			return new OR(s, fragments);
		}
		return null;
	}

	private Generic parseGeneric(Element root) {
		String s = root.getAttribute(ATTR_SEP);
		List<Fragment> fragments = parseChildrenFragment(root);
		if (!fragments.isEmpty()) {
			return new Generic(true, s, fragments);
		}
		return null;
	}

	private Static parseStatic(Node node) {
		String sql = SqlKit.trimLine(node.getTextContent());
		if (StringKit.isEmpty(sql)) {
			return null;
		}
		Sql pstmt = SqlDqlKit.parseSql(sql);
		return Static.getInstance(pstmt.content, pstmt.params);
	}

	/* Lv1 */
	static final String ELEM_MYSQL = "obase-mysql";
	static final String ELEM_TABLE = "table-class";
	static final String ELEM_META = "meta-class";
	static final String ELEM_STMT = "statement";
	/* Lv2 */
	static final String ELEM_WHERE = "where";
	static final String ELEM_AND = "and";
	static final String ELEM_OR = "or";
	static final String ELEM_X = "x";

	static final String ATTR_SEP = "s";
	static final String ATTR_NAMESPACE = "namespace";
	static final String ATTR_ID = "id";
	static final String ATTR_NOP = "nop";
}
