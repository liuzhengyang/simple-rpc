package com.github.liuzhengyang.simplerpc.core.bootstrap;

import com.github.liuzhengyang.simplerpc.core.proxy.ClientProxy;
import com.github.liuzhengyang.simplerpc.core.proxy.JdkClientProxy;
import com.github.liuzhengyang.simplerpc.core.transport.ClientImpl;
import com.google.common.base.Preconditions;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public class ClientBuilder<T> {
	private String serviceName;
	private String zkConn;
	private Class<T> serviceInterface;
	private int requestTimeoutMillis = 10000;
	private Class<? extends ClientProxy> clientProxyClass = JdkClientProxy.class;

	public static <T> ClientBuilder<T> builder() {
		ClientBuilder<T> clientBuilder = new ClientBuilder<T>();
		return clientBuilder;
	}

	public ClientBuilder<T> serviceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	public ClientBuilder<T> zkConn(String zkConn) {
		this.zkConn = zkConn;
		return this;
	}

	public ClientBuilder<T> serviceInterface(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
		return this;
	}

	public ClientBuilder<T> requestTimeout(int requestTimeoutMillis) {
		this.requestTimeoutMillis = requestTimeoutMillis;
		return this;
	}

	public ClientBuilder<T> clientProxyClass(Class<? extends ClientProxy> clientProxyClass) {
		this.clientProxyClass = clientProxyClass;
		return this;
	}

	public T build() {
		Preconditions.checkNotNull(serviceInterface);
		Preconditions.checkNotNull(zkConn);
		Preconditions.checkNotNull(serviceName);
		ClientImpl client = new ClientImpl(this.serviceName);
		client.setZkConn(this.zkConn);
		client.setRequestTimeoutMillis(this.requestTimeoutMillis);
		client.init();
		return client.proxyInterface(this.serviceInterface);
	}
}
