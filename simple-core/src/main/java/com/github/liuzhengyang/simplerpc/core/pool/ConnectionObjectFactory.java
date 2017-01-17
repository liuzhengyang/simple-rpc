package com.github.liuzhengyang.simplerpc.core.pool;

import com.github.liuzhengyang.simplerpc.core.RpcClientHandler;
import com.github.liuzhengyang.simplerpc.core.codec.ProtocolDecoder;
import com.github.liuzhengyang.simplerpc.core.codec.ProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-16
 */
public class ConnectionObjectFactory extends BasePooledObjectFactory<Channel> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionObjectFactory.class);


	private String ip;
	private int port;

	public ConnectionObjectFactory(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	private Channel connectNewChannel() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class)
				.group(new NioEventLoopGroup(1))
				.handler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
								.addLast(new ProtocolDecoder(10 * 1024 * 1024))
								.addLast(new ProtocolEncoder())
								.addLast(new RpcClientHandler())
						;
					}
				});
		try {
			final ChannelFuture f = bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000).connect(ip, port).sync();
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						LOGGER.info("Connect success {} ", f);
					}
				}
			});
			final Channel channel = f.channel();
			channel.closeFuture().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {

					LOGGER.info("Channel Close {} {}", ip, port);
//					channel.connect(new InetSocketAddress(ip, port)).sync();
//					ConnectionObjectFactory.this.destroyObject(ConnectionObjectFactory.this.wrap(channel));
//					Thread.sleep(1000);
				}
			});
			return channel;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Channel create() throws Exception {
		return connectNewChannel();
	}

	@Override
	public boolean validateObject(PooledObject<Channel> p) {
		Channel object = p.getObject();
		return object.isActive();
	}

	@Override
	public void destroyObject(PooledObject<Channel> p) throws Exception {
		p.getObject().close().addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				LOGGER.info("Close Finish");
			}
		});
	}

	public PooledObject<Channel> wrap(Channel obj) {
		return new DefaultPooledObject<Channel>(obj);
	}
}
