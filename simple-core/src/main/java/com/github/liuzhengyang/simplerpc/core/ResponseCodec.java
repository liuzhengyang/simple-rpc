package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.api.Serializer;
import com.github.liuzhengyang.simplerpc.common.Constants;
import com.github.liuzhengyang.simplerpc.serializer.*;
import com.github.liuzhengyang.simplerpc.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.TooLongFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class ResponseCodec extends ByteToMessageCodec<Response>{
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseCodec.class);

	private Serializer serializer = new KryoSerializer();

	protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
		LOGGER.info("Encode {}", msg);
		byte[] bytes = serializer.serialize(msg);
		int length = bytes.length;
		out.writeInt(length);
		ByteBuf byteBuf = out.writeBytes(bytes);
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int length = in.readInt();
		if (length > Constants.MAX_FRAME_LENGTH) {
			throw new TooLongFrameException();
		}
		byte[] buffer = new byte[length];
		in.readBytes(buffer);
		Response response = serializer.deserialize(Response.class, buffer);
		out.add(response);
		LOGGER.info("Decode Result: {}", response);
	}
}
