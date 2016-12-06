package com.github.obase.mysql;

import java.util.List;

/**
 * Data structure for paging query.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public class Page<T> {

	int start;
	int limit;

	String sortField;
	boolean sortDesc;

	int results;
	List<T> rows;

	public Page() {
		this(0, 0, null, false);
	}

	public Page(int start, int limit) {
		this(start, limit, null, false);
	}

	public Page(int start, int limit, String sortField, boolean sortDesc) {
		this.start = start;
		this.limit = limit;
		this.sortField = sortField;
		this.sortDesc = sortDesc;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public boolean isSortDesc() {
		return sortDesc;
	}

	public void setSortDesc(boolean sortDesc) {
		this.sortDesc = sortDesc;
	}

	public int getResults() {
		return results;
	}

	public void setResults(int results) {
		this.results = results;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

}
