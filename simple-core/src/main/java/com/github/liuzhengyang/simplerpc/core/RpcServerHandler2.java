package com.github.liuzhengyang.simplerpc.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class RpcServerHandler2 extends SimpleChannelInboundHandler<Object>{
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler2.class);

	private Object service;

	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		LOGGER.info("Receive {}", msg);
	}
}
