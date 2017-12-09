package com.github.obase.mysql.xml;

import java.util.LinkedList;
import java.util.List;

public final class ObaseMysqlObject {

	public String namespace;
	public List<Class<?>> tableClassList = new LinkedList<Class<?>>();
	public List<Class<?>> metaClassList = new LinkedList<Class<?>>();
	public List<Statement> statementList = new LinkedList<Statement>();

}
