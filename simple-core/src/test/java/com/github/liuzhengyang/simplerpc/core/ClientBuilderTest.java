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
		IHello hello = ClientBuilder.<IHello>builder().zkConn("127.0.0.1:2181")
				.serviceName("testBuilder").serviceInterface(IHello.class).build();
		for (int i = 0; i < 5; i++) {
			System.out.println(hello.say("Hello World!"));
		}
	}
}