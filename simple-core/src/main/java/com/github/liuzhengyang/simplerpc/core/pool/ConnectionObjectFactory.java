package com.github.liuzhengyang.simplerpc.core.pool;

import com.github.liuzhengyang.simplerpc.core.handler.ClientChannelInitializer;
import com.github.liuzhengyang.simplerpc.core.codec.ProtocolDecoder;
import com.github.liuzhengyang.simplerpc.core.codec.ProtocolEncoder;
import com.github.liuzhengyang.simplerpc.core.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
				.handler(new ClientChannelInitializer());
		try {
			final ChannelFuture f = bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.option(ChannelOption.TCP_NODELAY, true)
					.connect(ip, port).sync();
			final Channel channel = f.channel();
			addChannelListeners(f, channel);
			return channel;
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted", e);
			Thread.currentThread().interrupt();
		}
		return null;
	}

	private void addChannelListeners(final ChannelFuture f, Channel channel) {
		f.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					LOGGER.info("Connect success {} ", f);
				}
			}
		});
		channel.closeFuture().addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {

				LOGGER.info("Channel Close {} {}", ip, port);
			}
		});
	}

	public Channel create() throws Exception {
		// retry 3 times to connection channel
		for (int i = 0; i < 3; i++) {
			Channel channel = connectNewChannel();
			if (channel != null) {
				return channel;
			}
		}
		return null;
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
