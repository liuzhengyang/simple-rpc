package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.bootstrap.ClientBuilder;
import com.github.liuzhengyang.simplerpc.core.bootstrap.ServerBuilder;
import com.github.liuzhengyang.simplerpc.core.proxy.CglibClientProxy;
import com.github.liuzhengyang.simplerpc.core.proxy.ClientProxy;
import com.github.liuzhengyang.simplerpc.core.proxy.JdkClientProxy;
import com.github.liuzhengyang.simplerpc.core.transport.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public class ClientBuilderTest {
	static Server testBuilder;
	@BeforeClass
	public static void before() throws Exception {
		testBuilder = ServerBuilder.builder()
				.port(8998)
				.zkConn("127.0.0.1:2181")
				.serviceName("testBuilder")
				.serviceImpl(new HelloImpl())
				.build();
		testBuilder.start();
	}

	@AfterClass
	public static void after() throws Exception {
		testBuilder.shutdown();
	}

	@Test
	public void testClientWithBuilder() {
		proxyTest(null);
	}
	@Test
	public void testJdkProxy() {
		proxyTest(CglibClientProxy.class);
		proxyTest(JdkClientProxy.class);
	}

	private void proxyTest(Class<? extends ClientProxy> proxyClass) {
		IHello hello = ClientBuilder.<IHello>builder().zkConn("127.0.0.1:2181")
				.serviceName("testBuilder").clientProxyClass(proxyClass)
				.serviceInterface(IHello.class).build();
		System.out.println(hello);
		System.out.println(hello.toString());
		System.out.println(hello.hashCode());
		for (int i = 0; i < 5; i++) {
			Assert.assertEquals("return Hello World!", hello.say("Hello World!"));
		}
	}
}