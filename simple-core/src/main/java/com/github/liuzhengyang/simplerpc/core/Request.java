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
public class Request {
	private long requestId;
	private Class<?> clazz;
	private String method;
	private Class<?>[] parameterTypes;
	private Object[] params;
	private long requestTime;
}
