package com.github.obase.mysql;

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
	public void tail(T[] array) {
		for (T v : array) {
			tail(v);
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

}
