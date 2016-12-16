package com.github.liuzhengyang.simplerpc.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class ClientProxy {
	static InvocationHandler invocationHandler = new InvocationHandler() {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}
	};
	public static <T> T createProxy(Class<T> serviceClass) {

		T t = (T) Proxy.newProxyInstance(ClientProxy.class.getClassLoader(), serviceClass.getInterfaces(), invocationHandler);
		return t;
	}
}
