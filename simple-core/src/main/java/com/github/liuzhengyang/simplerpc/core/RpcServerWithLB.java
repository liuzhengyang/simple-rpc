package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.common.Config;
import com.github.liuzhengyang.simplerpc.core.lb.RegisterUtil;
import com.github.liuzhengyang.simplerpc.core.util.InetUtil;
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
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.liuzhengyang.simplerpc.common.Constants.ZK_BASE_PATH;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class RpcServerWithLB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerWithLB.class);

	private String ip;
	private int port;
	private boolean started = false;
	private Channel channel;
	private Object serviceImpl;
	private String serviceName;
	private String zkConn;

	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private CuratorFramework curatorFramework;
	public RpcServerWithLB(int port, Object serviceImpl, String serviceName) {
		this.port = port;
		this.serviceImpl = serviceImpl;
		this.serviceName = serviceName;
	}

	public String getZkConn() {
		return zkConn;
	}

	public void setZkConn(String zkConn) {
		this.zkConn = zkConn;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void init() {
		String zkConn = getZkConn();
		String localIp = InetUtil.getLocalIp();
		String ipPortStr = localIp + ":" + port;
		curatorFramework = CuratorFrameworkFactory.newClient(zkConn, new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();
		String serviceBasePath = ZK_BASE_PATH + "/services/" + serviceName;
		try {
			curatorFramework.create().creatingParentsIfNeeded().forPath(serviceBasePath);
		} catch (Exception e) {
			if (e.getMessage().contains("NodeExist")) {
				LOGGER.info("Path already Exist");
			} else {
				LOGGER.error("Create Path Error ", e);
				throw new RuntimeException("Register error");
			}
		}

		try {
			String s = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(serviceBasePath + "/" + ipPortStr);
		} catch (Exception e) {
			throw new RuntimeException("Register error");
		}
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
								.addLast(new LoggingHandler(LogLevel.INFO))
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
