package com.arronlong.redisweb.common.exception;

public class MethodNotSupportException extends RuntimeException {
	private static final long serialVersionUID = -8990490411346890568L;

	public MethodNotSupportException(String errormsg) {
		super(errormsg);
	}
}
