package com.github.obase.mysql.core;

/**
 * 辅助数据结构: 链结点
 */
public final class DLink<T> {

	public DNode<T> head; // 指向首元素
	public DNode<T> tail; // 指向尾元素

	public void tail(T value) {
		DNode<T> n = new DNode<T>();
		n.value = value;
		if (head == null) {
			head = n;
		}
		if (tail != null) {
			tail.next = n;
		}
		tail = n;
	}

	// 注意: 不能重用DNode, 会有隐患
	public void tail(DLink<T> link) {
		for (DNode<T> t = link.head; t != null; t = t.next) {
			tail(t.value);
		}
	}

	public void head(T value) {
		DNode<T> n = new DNode<T>();
		n.value = value;
		if (head != null) {
			n.next = head;
		}
		head = n;
		if (tail == null) {
			tail = n;
		}
	}

	public void chop(DNode<T> n) {
		n.next = null;
		tail = n;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(2048);
		sb.append('[');
		for (DNode<T> t = head; t != null; t = t.next) {
			sb.append(t.value).append(',');
		}
		int len = sb.length();
		if (len > 1) {
			sb.setCharAt(len - 1, ']');
		} else {
			sb.append(']');
		}

		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	static final DLink NIL = new DLink();

	@SuppressWarnings("unchecked")
	public static <T> DLink<T> nil() {
		return (DLink<T>) NIL;
	}

}
