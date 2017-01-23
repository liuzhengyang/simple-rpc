package com.github.liuzhengyang.simplerpc.exception;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-23
 */
public class RequestTimeoutException extends SimpleException{

	public RequestTimeoutException(String message) {
		super(message);
	}
}
