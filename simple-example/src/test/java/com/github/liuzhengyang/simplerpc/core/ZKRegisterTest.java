package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.lb.RegisterUtil;
import com.github.liuzhengyang.simplerpc.core.lb.ZKRegisterImpl;
import com.github.liuzhengyang.simplerpc.example.HelloImpl;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class ZKRegisterTest {

	@BeforeClass
	public static void beforeClass() {
		RegisterUtil.setImpl(new ZKRegisterImpl());
	}

	@Test
	public void testServerWithZK() {
		RpcServer rpcServer = new RpcServer(8090, new HelloImpl());
		rpcServer.init();
	}
}
