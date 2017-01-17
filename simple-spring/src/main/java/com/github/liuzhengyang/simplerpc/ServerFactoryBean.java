package com.github.liuzhengyang.simplerpc;

import com.github.liuzhengyang.simplerpc.core.RpcServerWithLB;
import org.springframework.beans.factory.FactoryBean;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-05
 */
public class ServerFactoryBean implements FactoryBean<Object> {

	// 远程调用的接口
	private Class<?> serviceInterface;
	private Object serviceImpl;
	private String ip;
	private int port;
	private String serviceName;
	private String zkConn;
	private RpcServerWithLB rpcServer;

	public Object getObject() throws Exception {
		return this;
	}

	public void start() {
		rpcServer = new RpcServerWithLB(port, serviceImpl, serviceName);
		rpcServer.setZkConn(getZkConn());
		rpcServer.init();
	}

	public void destroy() {
		rpcServer.stop();
	}

	public Class<?> getObjectType() {
		return this.getClass();
	}

	public boolean isSingleton() {
		return true;
	}


	public Class<?> getServiceInterface() {
		return serviceInterface;
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

	public Object getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public void setServiceImpl(Object serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public RpcServerWithLB getRpcServer() {
		return rpcServer;
	}

	public void setRpcServer(RpcServerWithLB rpcServer) {
		this.rpcServer = rpcServer;
	}

	public String getZkConn() {
		return zkConn;
	}

	public void setZkConn(String zkConn) {
		this.zkConn = zkConn;
	}
}
