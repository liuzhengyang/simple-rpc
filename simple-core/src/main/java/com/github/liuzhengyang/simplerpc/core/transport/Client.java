package com.github.liuzhengyang.simplerpc.core.transport;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public abstract class Client {
	public abstract void close();

	public abstract Response sendMessage(Class<?> clazz, Method method, Object[] args);

	public abstract <T> T proxyInterface(Class<T> serviceInterface);
}
