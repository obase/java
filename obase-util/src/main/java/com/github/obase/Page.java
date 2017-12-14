package com.github.obase;

import java.util.List;

/**
 * Data structure for paging.
 */
public class Page<T> {

	public final int start; // param _start
	public final int limit; // param _limit

	public final String field; // param _field
	public final boolean desc; // param _direction

	int results;
	List<T> rows;

	public Page() {
		this(0, 0, null, false);
	}

	public Page(int start, int limit) {
		this(start, limit, null, false);
	}

	public Page(int start, int limit, String field, boolean desc) {
		this.start = start;
		this.limit = limit;
		this.field = field;
		this.desc = desc;
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

	public int getStart() {
		return start;
	}

	public int getLimit() {
		return limit;
	}

	public String getField() {
		return field;
	}

	public boolean isDesc() {
		return desc;
	}

}
