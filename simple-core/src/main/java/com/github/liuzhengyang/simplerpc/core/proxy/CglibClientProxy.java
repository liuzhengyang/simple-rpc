package com.github.liuzhengyang.simplerpc.core.proxy;

import com.github.liuzhengyang.simplerpc.core.transport.Client;
import com.github.liuzhengyang.simplerpc.core.transport.Response;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-02-07s
 */
public class CglibClientProxy implements ClientProxy{
	private static class ProxyInteceptor implements MethodInterceptor {
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

		private Client client;

		private Class<?> serviceInterface;
		public ProxyInteceptor(Client client, Class<?> serviceInterface) {
			this.client = client;
			this.serviceInterface = serviceInterface;
		}

		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			if (hashCodeMethod.equals(method)) {
				return proxyHashCode(proxy);
			}
			if (equalsMethod.equals(method)) {
				return proxyEquals(proxy, args[0]);
			}
			if (toStringMethod.equals(method)) {
				return proxyToString(proxy);
			}
			return client.sendMessage(serviceInterface, method, args).getResponse();
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
	@Override
	public <T> T proxyInterface(Client client, Class<T> serviceInterface) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(serviceInterface);
		enhancer.setCallback(new ProxyInteceptor(client, serviceInterface));
		Object enhancedObject = enhancer.create();
		return (T)enhancedObject;
	}
}
