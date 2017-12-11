package com.github.obase.mysql.impl;

import java.util.Arrays;

/**
 * 辅助数据结构: 链结点
 */
public final class ParamBuilder {

	static final int DEF_INC = 64;
	static final int DEF_CAP = 64;
	static final int SET_NOIDX = -1; // 0未设置,-1标量设置,>0集合元素

	int cap;
	int len;
	String[] name;
	int[] set; //
	Object[] val;

	public ParamBuilder() {
		this(DEF_CAP);
	}

	public ParamBuilder(int cap) {
		this.cap = cap;
		this.name = new String[cap];
		this.set = new int[cap];
		this.val = new Object[cap];
	}

	public void append(String name) {
		ensure();
		this.name[this.len] = name;
		this.len++;
	}

	public void append(String name, Object val) {
		ensure();
		this.name[this.len] = name;
		this.set[this.len] = SET_NOIDX;
		this.val[this.len] = val;
		this.len++;
	}

	public void append(String name, int set, Object val) {
		ensure();
		this.name[this.len] = name;
		this.set[this.len] = set;
		this.val[this.len] = val;
		this.len++;
	}

	public void append(String[] params) {
		for (String name : params) {
			append(name);
		}
	}

	private void ensure() {
		if (this.len >= this.cap) {
			this.cap += DEF_INC;
			this.name = Arrays.copyOf(this.name, this.cap);
			this.set = Arrays.copyOf(this.set, this.cap);
			this.val = Arrays.copyOf(this.val, this.cap);
		}
	}

	public void setLength(int len) {
		// 压缩清除,避免泄露
		for (int i = len; i < this.len; i++) {
			name[i] = null;
			val[i] = null;
			set[i] = 0;
		}
		this.len = len;
	}

	public int length() {
		return this.len;
	}

}
