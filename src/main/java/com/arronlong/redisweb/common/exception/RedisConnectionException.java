package com.arronlong.redisweb.common.exception;

public class RedisConnectionException extends RuntimeException {
	private static final long serialVersionUID = -2981679325130079882L;

	public RedisConnectionException(String errormsg) {
		super(errormsg);
	}

}
