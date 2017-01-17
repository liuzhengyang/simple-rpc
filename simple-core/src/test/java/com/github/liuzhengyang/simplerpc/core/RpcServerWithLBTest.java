package com.github.liuzhengyang.simplerpc.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-10
 */
public class RpcServerWithLBTest {
	@Test
	public void init() throws Exception {
		RpcServerWithLB rpcServer = new RpcServerWithLB(8180, new HelloImpl(), "hello");
		rpcServer.setZkConn("127.0.0.1:2181");
		rpcServer.init();

		Thread.sleep(1000 * 1);
	}
	@Test
	public void init2() throws Exception {
		RpcServerWithLB rpcServer = new RpcServerWithLB(8181, new HelloImpl(), "hello");
		rpcServer.setZkConn("127.0.0.1:2181");
		rpcServer.init();
		Thread.sleep(1000 * 1);
	}

}