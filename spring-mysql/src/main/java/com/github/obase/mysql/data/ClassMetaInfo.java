package com.github.obase.mysql.data;

import java.util.LinkedHashMap;
import java.util.List;

public class ClassMetaInfo {

	public String internalName;

	public MetaAnnotation metaAnnotation;
	public TableAnnotation tableAnnotation;
	public PrimaryKeyAnnotation primaryKeyAnnotation;
	public List<ReferenceAnnotation> foreignKeyAnnotation;
	public List<IndexAnnotation> indexesAnnotation;
	public OptimisticLockAnnotation optimisticLockAnnotation;

	public final LinkedHashMap<String, FieldMetaInfo> fields = new LinkedHashMap<String, FieldMetaInfo>();
	public final LinkedHashMap<String, MethodMetaInfo> getters = new LinkedHashMap<String, MethodMetaInfo>(); // key is column name, but not property
	public final LinkedHashMap<String, MethodMetaInfo> setters = new LinkedHashMap<String, MethodMetaInfo>(); // key is column name, but not property

	public String tableName; // table name
	public List<String> keys; // primary key
	public List<String> columns; // column name

	public int hierarchies = 1;
}
