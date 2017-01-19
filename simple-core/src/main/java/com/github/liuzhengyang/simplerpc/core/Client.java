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
//	abstract void connect(String host, int port) throws IOException;
//	abstract void connect();
	abstract void close();
	abstract <T> T proxyInterface(Class<T> serviceInterface);
}
