package com.github.obase.mysql.xml;

import java.util.LinkedList;
import java.util.List;

import com.github.obase.mysql.stmt.Statement;

public final class ObaseMysqlObject {

	public String namespace;
	public List<Class<?>> tableClassList = new LinkedList<Class<?>>();
	public List<Class<?>> metaClassList = new LinkedList<Class<?>>();
	public List<Statement> statementList = new LinkedList<Statement>();

}
