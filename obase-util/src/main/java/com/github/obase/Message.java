package com.github.obase;

import java.io.Serializable;

public class Message<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public final String src;

	public final int errno;

	public final String errmsg;

	public final T data;

	public Message(String src, int errno, String errmsg, T data) {
		this.errno = errno;
		this.errmsg = errmsg;
		this.data = data;
		this.src = src;
	}

	public Message(String src, int errno, String errmsg) {
		this(src, errno, errmsg, null);
	}

	public Message(String src, T data) {
		this(src, 0, null, data);
	}

	public Message(int errno, String errmsg, T data) {
		this(null, errno, errmsg, data);
	}

	public Message(int errno, String errmsg) {
		this(null, errno, errmsg, null);
	}

	public Message(T data) {
		this(null, 0, null, data);
	}

	public String getSrc() {
		return src;
	}

	public int getErrno() {
		return errno;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public T getData() {
		return data;
	}

}
