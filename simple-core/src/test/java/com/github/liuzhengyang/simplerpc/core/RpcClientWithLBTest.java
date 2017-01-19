package com.github.liuzhengyang.simplerpc.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
		rpcServer.start();
	}

	@AfterClass
	public static void destroy() {
		if (rpcServer != null) {
			rpcServer.shutdown();
		}
	}
	@Test
	public void init() throws Exception {
		RpcClientWithLB rpcClientWithLB = new RpcClientWithLB("hello");
		rpcClientWithLB.setZkConn("127.0.0.1:2181");
		rpcClientWithLB.init();
		IHello iHello = rpcClientWithLB.proxyInterface(IHello.class);
		for (int i = 0; i < 20; i++) {
			Thread.sleep(100 * 5);
			iHello.say("hello world");
			if (i % 3 == 0) {
				destroy();
				before();
			}
		}
	}

}