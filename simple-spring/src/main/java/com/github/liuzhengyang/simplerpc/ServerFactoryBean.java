package com.github.liuzhengyang.simplerpc;

import com.github.liuzhengyang.simplerpc.core.RpcServer;
import org.springframework.beans.factory.FactoryBean;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-05
 */
public class ServerFactoryBean implements FactoryBean<RpcServer> {

	// 远程调用的接口
	private Class<?> serviceInterface;
	private Object serviceImpl;
	private String ip;
	private int port;
	private RpcServer rpcServer;


	public RpcServer getObject() throws Exception {
		rpcServer = new RpcServer(port, serviceImpl);
		return rpcServer;
	}

	public void start() {
		rpcServer.init();
	}

	public void destroy() {
		rpcServer.stop();
	}

	public Class<?> getObjectType() {
		return RpcServer.class;
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

	public RpcServer getRpcServer() {
		return rpcServer;
	}

	public void setRpcServer(RpcServer rpcServer) {
		this.rpcServer = rpcServer;
	}
}
