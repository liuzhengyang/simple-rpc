package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.bootstrap.ClientBuilder;
import com.github.liuzhengyang.simplerpc.core.transport.ClientImpl;
import com.github.liuzhengyang.simplerpc.core.transport.ServerImpl;
import org.junit.AfterClass;
import org.junit.Assert;
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
	private static ServerImpl rpcServer;

	@BeforeClass
	public static void before() throws Exception{
		rpcServer = new ServerImpl(8180, new HelloImpl(), "hello");
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
		ClientImpl rpcClientWithLB = new ClientImpl("hello");
		rpcClientWithLB.setZkConn("127.0.0.1:2181");
		rpcClientWithLB.init();
		IHello iHello = rpcClientWithLB.proxyInterface(IHello.class);
		Thread.sleep(100000);
		for (int i = 0; i < 20; i++) {
			Thread.sleep(100 * 5);
			iHello.say("hello world");
			if (i % 3 == 0) {
				destroy();
				before();
			}
		}
	}

	@Test
	public void testProxy() {
		IHello hello = ClientBuilder.<IHello>builder().zkConn("127.0.0.1:2181")
				.serviceName("testBuilder").serviceInterface(IHello.class).build();
		System.out.println(hello.toString());
		Assert.assertFalse(hello.equals(1));
		Assert.assertTrue(hello.equals(hello));
		System.out.println(hello.hashCode());
	}

}