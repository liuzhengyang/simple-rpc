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
public class RpcServerHandler extends SimpleChannelInboundHandler<Request> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

	private Object service;

	public RpcServerHandler(Object serviceImpl) {
		this.service = serviceImpl;
	}

	protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
		String methodName = msg.getMethod();
		Object[] params = msg.getParams();
		Class<?>[] parameterTypes = msg.getParameterTypes();
		long requestId = msg.getRequestId();
		Method method = service.getClass().getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		Object invoke = method.invoke(service, params);
		Response response = new Response();
		response.setRequestId(requestId);
		response.setResponse(invoke);
		ctx.pipeline().writeAndFlush(response);
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("Exception caught on {}, ", ctx.channel(), cause);
		ctx.channel().close();
	}
}
