package com.github.liuzhengyang.simplerpc.core;

import com.google.common.base.Splitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.liuzhengyang.simplerpc.core.ResponseContainer.responseMap;

/**
 * Description: 客户端代码
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class RpcClientWithLB implements IRpcClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientWithLB.class);

	private static AtomicLong atomicLong = new AtomicLong();
	// 发布的服务名称,用来寻找对应的服务提供者
	private String serviceName;
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
	private String zkConn;
	// 存放字符串Channel对应的map
	private ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();

	private static class ChannelWrapper {
		private String connectStr;
		private Channel channel;

		public String getConnectStr() {
			return connectStr;
		}

		public void setConnectStr(String connectStr) {
			this.connectStr = connectStr;
		}

		public Channel getChannel() {
			return channel;
		}

		public void setChannel(Channel channel) {
			this.channel = channel;
		}


	}

	public RpcClientWithLB(String serviceName) {
		this.serviceName = serviceName;
	}


	public String getZkConn() {
		return zkConn;
	}

	public void setZkConn(String zkConn) {
		this.zkConn = zkConn;
	}

	public void init() {


		// TODO 这段代码需要仔细检查重构整理
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(getZkConn(), new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();


		final GetChildrenBuilder children = curatorFramework.getChildren();
		try {
			final String serviceZKPath = "/simplerpc/services/" + serviceName;
			PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, serviceZKPath, true);
			pathChildrenCache.start();
			pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					LOGGER.info("Listen Event {}", event);
					List<String> newServiceData = children.forPath(serviceZKPath);
					LOGGER.info("Server {} list change {}", serviceName, newServiceData);
					// 关闭删除本地缓存中多出的channel
					for (Map.Entry<String, Channel> entry : channelMap.entrySet()) {
						String key = entry.getKey();
						Channel value = entry.getValue();
						if (!newServiceData.contains(key)) {
							value.close();
							LOGGER.info("Remove channel {}", key);
							channelMap.remove(key, value);
						}
					}

					for (String connStr : newServiceData) {
						if (!channelMap.containsKey(connStr)) {
							LOGGER.info("Add new Channel {}", connStr);
							addNewChannel(connStr);
						}
					}
				}
			});
			List<String> strings = children.forPath(serviceZKPath);
			if (CollectionUtils.isEmpty(strings)) {
				throw new RuntimeException("No Service available for " + serviceName);
			}

			LOGGER.info("Found Server {} List {}", serviceName, strings);
			for (String connStr : strings) {
				try {
					addNewChannel(connStr);
				} catch (Exception e) {
					LOGGER.error("Add New Channel Exception", e);
				}
			}

//			children.usingWatcher(new CuratorWatcher() {
//				public void process(WatchedEvent event) throws Exception {
//					Watcher.Event.EventType type = event.getType();
//					if (type == Watcher.Event.EventType.NodeChildrenChanged) {
//						String path = event.getPath();
//						if (serviceZKPath.equals(path)) {
//							List<String> newServiceData = children.forPath(serviceZKPath);
//							LOGGER.info("Server {} list change {}", serviceName, newServiceData);
//							if (CollectionUtils.isNotEmpty(newServiceData)) {
//								// 关闭删除本地缓存中多出的channel
//								for (Map.Entry<String, Channel> entry : channelMap.entrySet()) {
//									String key = entry.getKey();
//									Channel value = entry.getValue();
//									if (!newServiceData.contains(key)) {
//										value.close();
//										channelMap.remove(key, value);
//									}
//								}
//
//								for (String connStr : newServiceData) {
//									if (!channelMap.containsKey(connStr)) {
//										addNewChannel(connStr);
//									}
//								}
//							}
//						}
//					}
//				}
//			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Channel getNewChannel(final String serverIp, final int port) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class)
				.group(eventLoopGroup)
				.handler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
//								.addLast(new IdleStateHandler(30, 30, 30))
//								.addLast(new HeartBeatHandler())
								.addLast(new ResponseCodec())
								.addLast(new RpcClientHandler())
								.addLast(new RequestCodec())
						;
					}
				});
		try {
			ChannelFuture f = bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000).connect(serverIp, port).sync();
			Channel channel = f.channel();
			String connStr = serverIp + ":" + port;
			channelMap.put(connStr, channel);
			channel.closeFuture().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					Thread.sleep(1000);
					LOGGER.info("Reconnect {} {}", serverIp, port);
					addNewChannel(serverIp + ":" + port);
				}
			});
			return channel;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Channel reconnect(Channel channel) {
		Channel result = null;
		while(result == null) {
			InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
			String hostAddress = socketAddress.getAddress().getHostAddress();
			int port = socketAddress.getPort();
			result = addNewChannel(hostAddress + ":" + port);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private Channel addNewChannel(String connStr) {
		try {
			List<String> strings = Splitter.on(":").splitToList(connStr);
			if (strings.size() != 2) {
				throw new RuntimeException("Error connection str " + connStr);
			}
			String host = strings.get(0);
			int port = Integer.parseInt(strings.get(1));
			return getNewChannel(host, port);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Response sendMessage(Class<?> clazz, Method method, Object[] args) {
		Request request = new Request();
		request.setRequestId(atomicLong.incrementAndGet());
		request.setMethod(method.getName());
		request.setParams(args);
		request.setClazz(clazz);
		request.setParameterTypes(method.getParameterTypes());
		Channel channel = selectChannel();
		if (channel == null) {
			Response response = new Response();
			RuntimeException runtimeException = new RuntimeException("Channel is not active now");
			response.setThrowable(runtimeException);
			return response;
		}
		if (!channel.isActive()) {
			channel = reconnect(channel);
		}
		channel.writeAndFlush(request);
		BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<Response>(1);
		responseMap.put(request.getRequestId(), blockingQueue);
		try {
			return blockingQueue.poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			responseMap.remove(request.getRequestId());
		}
	}

	private Channel selectChannel() {
		Random random = new Random();
		int size = channelMap.size();
		if (size < 1) {
			return null;
		}
		int i = random.nextInt(size);
		List<Channel> channels = new ArrayList<Channel>(channelMap.values());
		return channels.get(i);
	}

	public <T> T newProxy(final Class<T> serviceInterface) {
		Object o = Proxy.newProxyInstance(RpcClientWithLB.class.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return sendMessage(serviceInterface, method, args).getResponse();
			}
		});
		return (T) o;
	}

	public void destroy() {
		try {
			for (Map.Entry<String, Channel> entry : channelMap.entrySet()) {
				Channel value = entry.getValue();
				value.close().sync();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public void notifyEvent(NotifyEvent notifyEvent) {

	}


	// TODO add jvm shutdown hook

}
