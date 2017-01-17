package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.pool.ConnectionObjectFactory;
import com.google.common.base.Splitter;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.liuzhengyang.simplerpc.core.ResponseHolder.responseMap;

/**
 * Description: 客户端代码
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class RpcClientWithLB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientWithLB.class);

	private static AtomicLong atomicLong = new AtomicLong();
	// 发布的服务名称,用来寻找对应的服务提供者
	private String serviceName;
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
	private String zkConn;
	private int requestTimeoutMillis = 10 * 1000;
	CuratorFramework curatorFramework;

	// 存放字符串Channel对应的map
	public static CopyOnWriteArrayList<ChannelWrapper> channelWrappers = new CopyOnWriteArrayList<ChannelWrapper>();

	private static class ChannelWrapper {
		private String connStr;
		private String host;
		private int ip;
		private Channel channel;
		private ObjectPool<Channel> channelObjectPool;

		public ChannelWrapper(String host, int port) {
			this.host = host;
			this.ip = port;
			this.connStr = host + ":" + ip;
			channelObjectPool = new GenericObjectPool<Channel>(new ConnectionObjectFactory(host, port));
		}

		public String getConnStr() {
			return connStr;
		}

		public void setConnStr(String connStr) {
			this.connStr = connStr;
		}

		public Channel getChannel() {
			return channel;
		}

		public void setChannel(Channel channel) {
			this.channel = channel;
		}

		public void close() {
			channelObjectPool.close();
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getIp() {
			return ip;
		}

		public void setIp(int ip) {
			this.ip = ip;
		}

		public ObjectPool<Channel> getChannelObjectPool() {
			return channelObjectPool;
		}

		public void setChannelObjectPool(ObjectPool<Channel> channelObjectPool) {
			this.channelObjectPool = channelObjectPool;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ChannelWrapper{");
			sb.append("connStr='").append(connStr).append('\'');
			sb.append(", host='").append(host).append('\'');
			sb.append(", ip=").append(ip);
			sb.append(", channel=").append(channel);
			sb.append(", channelObjectPool=").append(channelObjectPool);
			sb.append('}');
			return sb.toString();
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
		// 注册中心不可用时,保存本地缓存
		curatorFramework = CuratorFrameworkFactory.newClient(getZkConn(), new ExponentialBackoffRetry(1000, 3));
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
					for (ChannelWrapper cw : channelWrappers) {
						String connStr = cw.getConnStr();
						if (!newServiceData.contains(connStr)) {
							cw.close();
							LOGGER.info("Remove channel {}", connStr);
							channelWrappers.remove(cw);
						}
					}

					// 增加本地缓存中不存在的连接
					for (String connStr : newServiceData) {
						boolean containThis = false;
						for (ChannelWrapper cw : channelWrappers) {
							if (connStr != null && connStr.equals(cw.getConnStr())) {
								containThis = true;
							}
						}
						if (!containThis) {
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addNewChannel(String connStr) {
		try {
			List<String> strings = Splitter.on(":").splitToList(connStr);
			if (strings.size() != 2) {
				throw new RuntimeException("Error connection str " + connStr);
			}
			String host = strings.get(0);
			int port = Integer.parseInt(strings.get(1));
			ChannelWrapper channelWrapper = new ChannelWrapper(host, port);
			channelWrappers.add(channelWrapper);
			LOGGER.info("Add New Channel {}, {}", connStr, channelWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Response sendMessage(Class<?> clazz, Method method, Object[] args) {
		Request request = new Request();
		request.setRequestId(atomicLong.incrementAndGet());
		request.setMethod(method.getName());
		request.setParams(args);
		request.setClazz(clazz);
		request.setParameterTypes(method.getParameterTypes());
		ChannelWrapper channelWrapper = selectChannel();
		if (channelWrapper == null) {
			Response response = new Response();
			RuntimeException runtimeException = new RuntimeException("Channel is not active now");
			response.setThrowable(runtimeException);
			return response;
		}

		Channel channel = null;
		try {
			channel = channelWrapper.getChannelObjectPool().borrowObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (channel == null) {
			Response response = new Response();
			RuntimeException runtimeException = new RuntimeException("Channel is not available now");
			response.setThrowable(runtimeException);
			return response;
		}


		try {
			channel.writeAndFlush(request);
			BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<Response>(1);
			responseMap.put(request.getRequestId(), blockingQueue);
			return blockingQueue.poll(requestTimeoutMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				channelWrapper.getChannelObjectPool().returnObject(channel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			responseMap.remove(request.getRequestId());

		}
	}

	private ChannelWrapper selectChannel() {
		Random random = new Random();
		int size = channelWrappers.size();
		if (size < 1) {
			return null;
		}
		int i = random.nextInt(size);
		return channelWrappers.get(i);
	}

	public <T> T newProxy(final Class<T> serviceInterface) {
		// Fix JDK proxy  limitations and add other proxy implementation like cg-lib, spring proxy factory etc.
		Object o = Proxy.newProxyInstance(RpcClientWithLB.class.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return sendMessage(serviceInterface, method, args).getResponse();
				} catch (Exception e) {
					return null;
				}
			}
		});
		return (T) o;
	}

	public void destroy() {
		if (curatorFramework != null) {
			curatorFramework.close();
		}
		try {
			for (ChannelWrapper cw : channelWrappers) {
				cw.close();
			}
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public int getRequestTimeoutMillis() {
		return requestTimeoutMillis;
	}

	public void setRequestTimeoutMillis(int requestTimeoutMillis) {
		this.requestTimeoutMillis = requestTimeoutMillis;
	}

	// 启动一个后台任务, 定期检查服务器

	// TODO add jvm shutdown hook

}
