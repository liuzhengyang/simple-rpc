package com.github.liuzhengyang.simplerpc.core;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-10
 */
public class RpcClientWithLBTest {
	private static RpcServerWithLB rpcServer;

	@BeforeClass
	public static void before() throws Exception{
		rpcServer = new RpcServerWithLB(8180, new HelloImpl(), "hello");
		rpcServer.setZkConn("127.0.0.1:2181");
		rpcServer.init();
	}

	@AfterClass
	public static void destroy() {
		if (rpcServer != null) {
			rpcServer.stop();
		}
	}
	@Test
	public void init() throws Exception {
		RpcClientWithLB rpcClientWithLB = new RpcClientWithLB("hello");
		rpcClientWithLB.setZkConn("127.0.0.1:2181");
		rpcClientWithLB.init();
		IHello iHello = rpcClientWithLB.newProxy(IHello.class);
		for (int i = 0; i < 100; i++) {
			Thread.sleep(10 * 5);
			iHello.say("hello world");
		}
	}

}