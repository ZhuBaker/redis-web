package com.arronlong.redisweb.common.exception;


public class RedisInitException extends RuntimeException {
	private static final long serialVersionUID = 2128051948609698105L;

	public RedisInitException(Exception e) {
		super(e);
	}

	public RedisInitException(String msg, Throwable e1) {
		super(msg, e1);
	}
	
}
