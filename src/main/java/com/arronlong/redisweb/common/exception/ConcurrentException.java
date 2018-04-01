package com.arronlong.redisweb.common.exception;

public class ConcurrentException extends RuntimeException {
	private static final long serialVersionUID = 9039442304816409853L;

	public ConcurrentException(String msg) {
		super(msg);
	}
	
}
