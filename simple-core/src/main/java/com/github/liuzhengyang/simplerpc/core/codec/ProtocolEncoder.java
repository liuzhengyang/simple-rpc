package com.github.liuzhengyang.simplerpc.core.codec;

import com.github.liuzhengyang.simplerpc.api.Serializer;
import com.github.liuzhengyang.simplerpc.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-16
 */
public class ProtocolEncoder extends MessageToByteEncoder<Object>{
	private Serializer serializer = new KryoSerializer();

	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		byte[] bytes = serializer.serialize(msg);
		int length = bytes.length;
		out.writeInt(length);
		out.writeBytes(bytes);
	}

}
