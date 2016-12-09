package com.github.obase;

public final class MessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String src;
	private final int errno;

	public MessageException(String errmsg) {
		this(null, Message.ERRNO_UNDEFINED, errmsg);
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

	@SuppressWarnings("rawtypes")
	public Message toMessage() {
		return new Message(src, errno, super.getMessage());
	}
}
