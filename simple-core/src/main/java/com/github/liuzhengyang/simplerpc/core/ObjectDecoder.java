package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.api.Serializer;
import com.github.liuzhengyang.simplerpc.common.Constants;
import com.github.liuzhengyang.simplerpc.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-11
 */
public class ObjectDecoder extends LengthFieldBasedFrameDecoder {
	private Serializer serializer = new KryoSerializer();
	public ObjectDecoder() {
		super(1024 * 1024, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
		// TODO Avoid byte copy
		int i = byteBuf.readableBytes();
		if (i > Constants.MAX_FRAME_LENGTH) {
			throw new TooLongFrameException("Length " + i);
		}
		byte[] bytes = new byte[i];
		byteBuf.readBytes(bytes);
		return serializer.deserialize(bytes);
	}
}
