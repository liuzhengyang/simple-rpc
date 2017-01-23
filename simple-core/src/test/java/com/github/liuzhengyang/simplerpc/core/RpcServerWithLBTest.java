package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.transport.ServerImpl;
import org.junit.Test;

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
		ServerImpl rpcServer = new ServerImpl(8182, new HelloImpl(), "hello");
		rpcServer.setZkConn("127.0.0.1:2181");
		rpcServer.start();
	}

	@Test
	public void init2() throws Exception {
		ServerImpl rpcServer = new ServerImpl(8181, new HelloImpl(), "hello");
		rpcServer.setZkConn("127.0.0.1:2181");
		rpcServer.start();
	}

}