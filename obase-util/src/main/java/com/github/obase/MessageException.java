package com.github.obase;

/**
 * @author Administrator
 *
 */
public final class MessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final int ERRNO_UNKNOWN_ERROR = -1;
	private final String src;
	private final int errno;

	public MessageException(String errmsg) {
		this(null, ERRNO_UNKNOWN_ERROR, errmsg);
	}

	public MessageException(int errno, String errmsg) {
		this(null, errno, errmsg);
	}

	public MessageException(String src, int errno, String errmsg) {
		super(errmsg);
		this.src = src;
		this.errno = errno;
	}

	public int getErrno() {
		return errno;
	}

	public String getErrmsg() {
		return super.getMessage();
	}

	public String getSrc() {
		return src;
	}

}
