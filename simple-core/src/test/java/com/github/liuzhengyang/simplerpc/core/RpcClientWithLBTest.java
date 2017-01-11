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
public class RpcClientWithLBTest {
	@Test
	public void init() throws Exception {
		RpcClientWithLB rpcClientWithLB = new RpcClientWithLB("hello");
		rpcClientWithLB.setZkConn("127.0.0.1:2181");
		rpcClientWithLB.init();
		IHello iHello = rpcClientWithLB.newProxy(IHello.class);
		for (int i = 0; i < 100; i++) {
			Thread.sleep(1000 * 5);
			iHello.say("hello world");
		}
	}

}