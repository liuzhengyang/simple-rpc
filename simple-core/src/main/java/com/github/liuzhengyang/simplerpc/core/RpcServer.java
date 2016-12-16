package com.github.liuzhengyang.simplerpc.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class RpcServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private String ip;
	private int port;
	private boolean started = false;
	private Channel channel;
	private Object serviceImpl;

	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	public RpcServer(int port, Object serviceImpl) {
		this.port = port;
		this.serviceImpl = serviceImpl;
	}

	public void init() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
								.addLast(new LoggingHandler(LogLevel.INFO))
//								.addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4))
//								.addLast(new LengthFieldPrepender(4))
								.addLast(new RequestCodec())
								.addLast(new RpcServerHandler(serviceImpl))
								.addLast(new ResponseCodec())
						;
					}
				});
		try {
			ChannelFuture sync = bootstrap.bind(port).sync();
			LOGGER.info("Server Started At {}", port);
			started = true;
			this.channel = sync.channel();
			sync.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
