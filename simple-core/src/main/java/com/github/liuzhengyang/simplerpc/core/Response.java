package com.github.liuzhengyang.simplerpc.core;

import lombok.Data;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
@Data
public class Response {
	private long requestId;
	private Object response;
	private Throwable throwable;
}
