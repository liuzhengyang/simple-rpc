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
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.liuzhengyang.simplerpc.core.RpcClientWithLB.channelMap;

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

	public Channel create() throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class)
				.group(new NioEventLoopGroup(1))
				.handler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
//								.addLast(new IdleStateHandler(30, 30, 30))
//								.addLast(new HeartBeatHandler())
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
			Channel channel = f.channel();
			String connStr = ip + ":" + port;
//			channelMap.put(connStr, channel);
			channel.closeFuture().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					Thread.sleep(1000);
					LOGGER.info("Try to reconnect {} {}", ip, port);
//					addNewChannel(serverIp + ":" + port);
				}
			});
			return channel;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PooledObject<Channel> wrap(Channel obj) {
		return new DefaultPooledObject<Channel>(obj);
	}
}
