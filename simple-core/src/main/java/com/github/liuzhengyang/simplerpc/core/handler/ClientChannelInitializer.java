package com.github.liuzhengyang.simplerpc.core.handler;

import com.github.liuzhengyang.simplerpc.core.codec.ProtocolDecoder;
import com.github.liuzhengyang.simplerpc.core.codec.ProtocolEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-03-01
 */
public class ClientChannelInitializer extends ChannelInitializer<Channel> {
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
				.addLast(new ProtocolDecoder(10 * 1024 * 1024))
				.addLast(new ProtocolEncoder())
				.addLast(new RpcClientHandler())
		;
	}
}
