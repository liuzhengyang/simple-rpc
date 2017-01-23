package com.github.liuzhengyang.simplerpc;

import com.github.liuzhengyang.simplerpc.core.bootstrap.ClientBuilder;
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
	private String serviceName;
	private String zkConn;

	public T getObject() {
		return ClientBuilder.<T>builder().zkConn(zkConn)
				.serviceName(serviceName)
				.serviceInterface(serviceInterface)
				.build();
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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getZkConn() {
		return zkConn;
	}

	public void setZkConn(String zkConn) {
		this.zkConn = zkConn;
	}
}
