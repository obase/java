package com.github.obase.mysql;

import java.util.List;

/**
 * Data structure for paging query.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public class Page<T> {

	int offset;
	int count;

	String orderBy;
	boolean orderDesc;

	int total;
	List<T> data;

	public Page() {
		this(0, 0, null, false);
	}

	public Page(int start, int max) {
		this(start, max, null, false);
	}

	public Page(int start, int max, String orderBy, boolean orderDesc) {
		this.offset = start;
		this.count = max;
		this.orderBy = orderBy;
		this.orderDesc = orderDesc;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(boolean orderDesc) {
		this.orderDesc = orderDesc;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
