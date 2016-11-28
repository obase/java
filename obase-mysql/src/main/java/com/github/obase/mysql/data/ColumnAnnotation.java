package com.github.obase.mysql.data;

import com.github.obase.mysql.annotation.SqlType;

public class ColumnAnnotation {

	public String name; // Using field name as default;

	public String comment;

	public SqlType type;

	public Integer length;

	public Integer decimals;

	public Boolean notNull;

	public Boolean unique;

	public Boolean autoIncrement; // require primaryKey > 0

	public Boolean key;

	public String defaultValue; // default value as string
}
