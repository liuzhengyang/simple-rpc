package com.github.liuzhengyang.simplerpc.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class RequestEncoder extends MessageToByteEncoder<Request>{
	protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
		byte[] serialize = Serializer.serialize(msg);
		out.writeBytes(serialize);
	}
}
