package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.bootstrap.ServerBuilder;
import com.github.liuzhengyang.simplerpc.core.transport.Server;
import org.junit.Test;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public class ServerBuilderTest {

	@Test
	public void testServerSetWithBuilder() throws InterruptedException {
		Server testBuilder = ServerBuilder.builder()
				.port(8998)
				.zkConn("127.0.0.1:2181")
				.serviceName("testBuilder")
				.serviceImpl(new HelloImpl())
				.build();
		testBuilder.start();
		Thread.sleep(1000 * 3);
		testBuilder.shutdown();
	}

}