package com.github.obase;

import java.util.List;

/**
 * Data structure for paging.
 */
public class Page<T> {

	public final int start; // param _start
	public final int limit; // param _limit

	public final String field; // param _field
	public final String direction; // param _direction

	int results;
	List<T> rows;

	public Page() {
		this(0, 0, null, null);
	}

	public Page(int start, int limit) {
		this(start, limit, null, null);
	}

	public Page(int start, int limit, String field, String direction) {
		this.start = start;
		this.limit = limit;
		this.field = field;
		this.direction = direction;
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

	public String getDirection() {
		return direction;
	}

}
