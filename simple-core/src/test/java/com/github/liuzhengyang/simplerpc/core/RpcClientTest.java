package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.example.IHello;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class RpcClientTest {
	@Test
	public void init() throws Exception {
		RpcClient rpcClient = new RpcClient("127.0.0.1", 8090);
		rpcClient.init();
		IHello ihello = rpcClient.newProxy(IHello.class);
		String nihaoya = ihello.say("nihaoya");
		System.out.println(nihaoya);
		Thread.sleep(100000);
//		rpcClient.destroy();
	}

	@Test
	public void sendMessage() throws Exception {

	}

}