package com.github.obase.mysql.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigMetaInfo {

	public final String namespace;
	public final List<String> tables = new LinkedList<String>();
	public final List<String> metas = new LinkedList<String>();
	public final Map<String, String> sqls = new LinkedHashMap<String, String>();

	public ConfigMetaInfo(String namespace) {
		this.namespace = namespace;
	}

	public void addPsql(String id, String value) {
		sqls.put(namespace + '.' + id, value); // key=namespace+'.'+id;
	}

}
