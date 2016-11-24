package com.github.obase.webc;

/**
 * @author Administrator
 *
 */
public final class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String src;
	private final int errno;

	public ApplicationException(int errno, String errmsg, Throwable t) {
		this(null, errno, errmsg, t);
	}

	public ApplicationException(String src, int errno, String errmsg) {
		this(src, errno, errmsg, null);
	}

	public ApplicationException(String src, int errno, String errmsg, Throwable t) {
		super(errmsg, t);
		this.src = src;
		this.errno = errno;
	}

	public ApplicationException(String errmsg, Throwable t) {
		this(Webc.ERRNO_UNKNOWN_ERROR, errmsg, t);
	}

	public ApplicationException(Throwable t) {
		this(Webc.ERRNO_UNKNOWN_ERROR, t.getMessage(), t);
	}

	public ApplicationException(String errmsg) {
		this(Webc.ERRNO_UNKNOWN_ERROR, errmsg, null);
	}

	public ApplicationException(int errno, String errmsg) {
		this(errno, errmsg, null);
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
