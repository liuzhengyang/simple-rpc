package com.github.liuzhengyang.simplerpc.core;

import java.io.IOException;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public abstract class Client {
	public abstract void close();
	public abstract <T> T proxyInterface(Class<T> serviceInterface);
}
