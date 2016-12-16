package com.github.liuzhengyang.simplerpc.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class RequestCodec extends ByteToMessageCodec<Request>{
	protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
		byte[] bytes = Serializer.serialize(msg);
		int length = bytes.length;
		out.writeInt(length);
		ByteBuf byteBuf = out.writeBytes(bytes);
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int length = in.readInt();
		byte[] buffer = new byte[length];
		in.readBytes(buffer);
		Request request = Serializer.deserialize(Request.class, buffer);
		out.add(request);
	}
}
