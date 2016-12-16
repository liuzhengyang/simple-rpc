package com.github.liuzhengyang.simplerpc.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class RequestDecoder extends ByteToMessageDecoder {

	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

	}
}
