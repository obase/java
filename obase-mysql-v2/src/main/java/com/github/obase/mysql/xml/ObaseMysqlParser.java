package com.github.obase.mysql.xml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.github.obase.MessageException;
import com.github.obase.kit.StringKit;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.Pstmt;
import com.github.obase.mysql.stmt.Cdata;
import com.github.obase.mysql.stmt.Foreach;
import com.github.obase.mysql.stmt.Isnull;
import com.github.obase.mysql.stmt.Notnull;
import com.github.obase.mysql.stmt.Statement;
import com.github.obase.mysql.stmt.Whenall;
import com.github.obase.mysql.stmt.Whenany;
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

	public ObaseMysqlObject parse(InputStream is) throws Exception {

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
				parseStmt(obj, (Element) node);
			}
		}

		return obj;
	}

	void parseTable(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		obj.tableClassList.add(Class.forName(className));
	}

	void parseMeta(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		obj.metaClassList.add(Class.forName(className));
	}

	void parseStmt(ObaseMysqlObject obj, Element root) {
		String id = root.getAttribute(ATTR_ID);
		String nop = root.getAttribute(ATTR_NOP);
		List<Fragment> fragments = parseContainerFragments(root);
		if (!fragments.isEmpty()) {
			obj.statementList.add(new Statement(id, "true".equalsIgnoreCase(nop), fragments));
		}
	}

	private List<Fragment> parseContainerFragments(Element root) {
		List<Fragment> fragments = new LinkedList<Fragment>();

		for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
			short nt = node.getNodeType();
			Fragment f = null;
			if (nt == Node.TEXT_NODE) {
				f = parseCdata(node);
			} else if (nt == Node.ELEMENT_NODE) {
				String tag = node.getNodeName();
				if (ELEM_WHENALL.equals(tag)) {
					f = parseWhenall((Element) node);
				} else if (ELEM_WHENANY.equals(tag)) {
					f = parseWhenany((Element) node);
				} else if (ELEM_ISNULL.equals(tag)) {
					f = parseIsnull((Element) node);
				} else if (ELEM_NOTNULL.equals(tag)) {
					f = parseNotnull((Element) node);
				} else if (ELEM_FOREACH.equals(tag)) {
					f = parseForeach((Element) node);
				}
			}
			if (f != null) {
				fragments.add(f);
			}
		}
		return fragments;
	}

	private Foreach parseForeach(Element node) {

		String sql = SqlKit.filterWhiteSpaces(node.getTextContent());
		if (StringKit.isEmpty(sql)) {
			return null;
		}
		Pstmt pstmt = SqlDqlKit.parsePstmt(sql);

		// 目前只支持一个参数
		int size = pstmt.param.size();
		if (size > 1) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_EXCEED_PARAMS, "foreach element nested more than 1 params: " + pstmt);
		}
		return new Foreach(pstmt.psql, size > 0 ? pstmt.param.get(0) : null, node.getAttribute(ATTR_SEP));
	}

	private Notnull parseNotnull(Element node) {

		String sql = SqlKit.filterWhiteSpaces(node.getTextContent());
		if (StringKit.isEmpty(sql)) {
			return null;
		}
		Pstmt pstmt = SqlDqlKit.parsePstmt(sql);

		// 目前只支持一个参数
		int size = pstmt.param.size();
		if (size > 1) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_EXCEED_PARAMS, "notnull element nested more than 1 params: " + pstmt);
		}
		return new Notnull(pstmt.psql, size > 0 ? pstmt.param.get(0) : null);
	}

	private Isnull parseIsnull(Element node) {
		String sql = SqlKit.filterWhiteSpaces(node.getTextContent());
		if (StringKit.isEmpty(sql)) {
			return null;
		}
		Pstmt pstmt = SqlDqlKit.parsePstmt(sql);

		// 目前只支持一个参数
		int size = pstmt.param.size();
		if (size > 1) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_EXCEED_PARAMS, "notnull element nested more than 1 params: " + pstmt);
		}
		return new Isnull(pstmt.psql, size > 0 ? pstmt.param.get(0) : null);
	}

	private Whenany parseWhenany(Element root) {
		List<Fragment> fragments = parseContainerFragments(root);
		// 如果没有子元素,没有必要构建
		return fragments.isEmpty() ? null : new Whenany(fragments);
	}

	private Whenall parseWhenall(Element root) {
		List<Fragment> fragments = parseContainerFragments(root);
		// 如果没有子元素,没有必要构建
		return fragments.isEmpty() ? null : new Whenall(fragments);
	}

	private Cdata parseCdata(Node node) {
		String sql = SqlKit.filterWhiteSpaces(node.getTextContent());
		if (StringKit.isEmpty(sql)) {
			return null;
		}
		Pstmt pstmt = SqlDqlKit.parsePstmt(sql);
		return new Cdata(pstmt.psql, pstmt.param);
	}

	/* Lv1 */
	static final String ELEM_MYSQL = "obase-mysql";
	static final String ELEM_TABLE = "table-class";
	static final String ELEM_META = "meta-class";
	static final String ELEM_STMT = "statement";
	/* Lv2 */
	static final String ELEM_WHENALL = "whenall";
	static final String ELEM_WHENANY = "whenany";
	static final String ELEM_ISNULL = "isnull";
	static final String ELEM_NOTNULL = "notnall";
	static final String ELEM_FOREACH = "foreach";

	static final String ATTR_SEP = "sep";
	static final String ATTR_NAMESPACE = "namesapce";
	static final String ATTR_ID = "id";
	static final String ATTR_NOP = "nop";
}
