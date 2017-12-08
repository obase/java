package com.github.obase.mysql.xml;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.github.obase.kit.StringKit;
import com.github.obase.mysql.core.DLink;
import com.github.obase.mysql.core.DNode;
import com.github.obase.mysql.core.Part;
import com.github.obase.mysql.stmt.AND;
import com.github.obase.mysql.stmt.OR;
import com.github.obase.mysql.stmt.Param;
import com.github.obase.mysql.stmt.Statement;
import com.github.obase.mysql.stmt.Static;
import com.github.obase.mysql.stmt.WHERE;
import com.github.obase.mysql.stmt.X;
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

	private DLink<Part> optimize(DLink<Part> origs) {

		DLink<Part> ret = new DLink<Part>();
		DLink<String> psql = new DLink<String>();
		DLink<Param> params = new DLink<Param>();
		// 合并优化静态,如果无动态则psqls与params存的是静态
		for (DNode<Part> t = origs.head; t != null; t = t.next) {
			Part f = t.value;
			if (f.isDynamic()) {
				if (psql.head != null) {
					ret.tail(new Static(psql.toString(), params));
					psql = new DLink<String>();
					params = new DLink<Param>();
				}
				ret.tail(f);
			} else {
				String s = f.getPsql();
				if (StringKit.isNotBlank(s)) { // 空元素去除
					psql.tail(s);
					params.tail(f.getParams());
				}
			}
		}
		if (psql.head != null) {
			ret.tail(new Static(psql.toString(), params));
		}

		return ret;
	}

	void parseTable(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		if (StringKit.isNotEmpty(className)) {
			obj.tableClassList.add(Class.forName(className));
		}
	}

	void parseMeta(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		if (StringKit.isNotEmpty(className)) {
			obj.metaClassList.add(Class.forName(className));
		}
	}

	void parseStatement(ObaseMysqlObject obj, Element root) {
		String id = root.getAttribute(ATTR_ID);
		String nop = root.getAttribute(ATTR_NOP);
		DLink<Part> childs = parseChildPart(root);
		if (childs.head != null) {
			obj.statementList.add(new Statement(id, "true".equalsIgnoreCase(nop), childs));
		}
	}

	private DLink<Part> parseChildPart(Element root) {
		DLink<Part> ret = new DLink<Part>();

		for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
			short nt = node.getNodeType();
			Part f = null;
			if (nt == Node.TEXT_NODE) {
				f = parseStatic(node);
			} else if (nt == Node.ELEMENT_NODE) {
				String tag = node.getNodeName();
				if (ELEM_WHERE.equals(tag)) {
					f = parseX((Element) node, new WHERE());
				} else if (ELEM_AND.equals(tag)) {
					f = parseX((Element) node, new AND());
				} else if (ELEM_OR.equals(tag)) {
					f = parseX((Element) node, new OR());
				} else if (ELEM_X.equals(tag)) {
					f = parseX((Element) node, new X());
				}
			}
			if (f != null) {
				ret.tail(f);
			}
		}
		if (ret.head != null) {
			ret = optimize(ret);
		}
		return ret;
	}

	private Part parseX(Element root, X x) {
		String s = root.getAttribute(ATTR_SEP);
		DLink<Part> parts = parseChildPart(root);
		if (parts.head == null) {
			return null;
		}
		if (parts.head == parts.tail) {
			// 不或只包含一个子元素
			Part p = parts.head.value;
			DNode<Param> ph = p.getParams().head;
			return ph == null ? new Static(p.getPsql(), null) : x.reset(s, p.getPsql(), ph.value);
		} else {
			// 包含多个子标签
			return x.reset(s, parts);
		}

	}

	private Part parseStatic(Node node) {
		String val = SqlKit.filterWhiteSpaces(node.getTextContent());
		if (StringKit.isEmpty(val)) {
			return null;
		}
		Sql sql = SqlDqlKit.parseSql(val);
		return Static.getInstance(sql.content, sql.params);
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
