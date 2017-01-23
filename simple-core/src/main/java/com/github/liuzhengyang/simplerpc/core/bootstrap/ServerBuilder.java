package com.github.liuzhengyang.simplerpc.core.bootstrap;

import com.github.liuzhengyang.simplerpc.core.transport.Server;
import com.github.liuzhengyang.simplerpc.core.transport.ServerImpl;
import com.google.common.base.Preconditions;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public class ServerBuilder {
	private int port;
	private String serviceName;
	private Object serviceImpl;
	private String zkConn;

	private ServerBuilder() {}

	public static ServerBuilder builder() {
		ServerBuilder serverBuilder = new ServerBuilder();
		return serverBuilder;
	}

	public ServerBuilder port(int port) {
		this.port = port;
		return this;
	}

	public ServerBuilder serviceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	public ServerBuilder serviceImpl(Object serviceImpl) {
		this.serviceImpl = serviceImpl;
		return this;
	}

	public ServerBuilder zkConn(String zkConn) {
		this.zkConn = zkConn;
		return this;
	}

	public Server build() {
		Preconditions.checkNotNull(serviceImpl);
		Preconditions.checkNotNull(serviceName);
		Preconditions.checkNotNull(zkConn);
		Preconditions.checkArgument(port > 0);
		ServerImpl rpcServerWithLB = new ServerImpl(this.port, this.serviceImpl, this.serviceName, this.zkConn);
		return rpcServerWithLB;
	}
}
