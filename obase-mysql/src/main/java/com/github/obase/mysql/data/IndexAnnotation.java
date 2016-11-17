package com.github.obase.mysql.data;

import java.util.List;

import com.github.obase.mysql.annotation.IndexType;
import com.github.obase.mysql.annotation.Using;

public class IndexAnnotation {

	public String name;

	public List<String> columns;

	public IndexType type;

	public Using using;
}
