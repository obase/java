package com.github.obase;

import java.io.Serializable;

public class Message<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int ERRNO_UNDEFINED = -1;

	private String src;

	private int errno;

	private String errmsg;

	private T data;

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

	public Message() {
		this(null, 0, null, null);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getErrno() {
		return errno;
	}

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
