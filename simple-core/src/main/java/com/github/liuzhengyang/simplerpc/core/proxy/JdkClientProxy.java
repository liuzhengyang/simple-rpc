package com.github.liuzhengyang.simplerpc.core.proxy;

import com.github.liuzhengyang.simplerpc.core.Client;
import com.github.liuzhengyang.simplerpc.core.RpcClientWithLB;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-23
 */
public class JdkClientProxy implements ClientProxy {
	private static Method hashCodeMethod;
	private static Method equalsMethod;
	private static Method toStringMethod;

	static {
		try {
			hashCodeMethod = Object.class.getMethod("hashCode");
			equalsMethod = Object.class.getMethod("equals", Object.class);
			toStringMethod = Object.class.getMethod("toString");
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}


	@Override
	public <T> T proxyInterface(final Client client, final Class<T> serviceInterface) {
		// Fix JDK proxy  limitations and add other proxy implementation like cg-lib, spring proxy factory etc.
		Object proxyInstance = Proxy.newProxyInstance(RpcClientWithLB.class.getClassLoader(),
				new Class[]{serviceInterface}, new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.equals(hashCodeMethod)) {
							return proxyHashCode(proxy);
						}
						if (method.equals(equalsMethod)) {
							return proxyEquals(proxy, args[0]);
						}
						if (method.equals(toStringMethod)) {
							return proxyToString(proxy);
						}
						try {
							return client.sendMessage(serviceInterface, method, args).getResponse();
						} catch (Exception e) {
							// TODO RPC invoke exception handle
							throw new RuntimeException(e);
						}
					}
				});
		return (T) proxyInstance;
	}

	private int proxyHashCode(Object proxy) {
		return System.identityHashCode(proxy);
	}

	private boolean proxyEquals(Object proxy, Object other) {
		return (proxy == other);
	}

	private String proxyToString(Object proxy) {
		return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
	}
}
