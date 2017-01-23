package com.github.liuzhengyang.simplerpc.core.codec;

import com.github.liuzhengyang.simplerpc.serializer.Serializer;
import com.github.liuzhengyang.simplerpc.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-16
 */
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolDecoder.class);

	private Serializer serializer = new KryoSerializer();
	public ProtocolDecoder(int maxFrameLength) {
		super(maxFrameLength, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf decode = (ByteBuf) super.decode(ctx, in);
		if (decode != null) {
			int byteLength = decode.readableBytes();
			// TODO try to avoid data copy
			byte[] byteHolder = new byte[byteLength];
			decode.readBytes(byteHolder);
			Object deserialize = serializer.deserialize(byteHolder);
			return deserialize;
		}
		LOGGER.debug("Decoder Result is null");
		return null;
	}
}
