package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.api.Serializer;
import com.github.liuzhengyang.simplerpc.serializer.KryoSerializer;
import com.github.liuzhengyang.simplerpc.serializer.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-11
 */
public class ObjectEncoder extends MessageToByteEncoder<Object>{
	private Serializer serializer = new KryoSerializer();
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		byte[] serialize = serializer.serialize(msg);
		out.writeInt(serialize.length);
		out.writeBytes(serialize);
	}
}
