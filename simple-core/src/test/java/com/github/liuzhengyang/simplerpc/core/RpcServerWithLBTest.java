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
		RpcServerWithLB rpcServer = new RpcServerWithLB(8080, new HelloImpl(), "hello");
		rpcServer.init();

		Thread.sleep(1000 * 1000);
	}
	@Test
	public void init2() throws Exception {
		RpcServerWithLB rpcServer = new RpcServerWithLB(8081, new HelloImpl(), "hello");
		rpcServer.init();
		Thread.sleep(1000 * 1000);
	}

}