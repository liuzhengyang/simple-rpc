package com.github.liuzhengyang.simplerpc;

import com.github.liuzhengyang.simplerpc.core.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-05
 */
public class ClientFactoryBean<T> implements FactoryBean<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactoryBean.class);

	private Class<T> serviceInterface;
	private String ip;
	private int port;

	public T getObject() {
		RpcClient rpcClient = new RpcClient(ip, port);
		rpcClient.init();

		return rpcClient.newProxy(serviceInterface);
	}

	public Class<?> getObjectType() {
		return serviceInterface;
	}

	public boolean isSingleton() {
		return true;
	}

	public Class<T> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
