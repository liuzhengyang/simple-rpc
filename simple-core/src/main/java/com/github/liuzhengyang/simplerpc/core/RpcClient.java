package com.github.liuzhengyang.simplerpc.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class RpcClient implements IRpcClient{
	private static AtomicLong atomicLong = new AtomicLong();
	private String serverIp;
	private int port;
	private boolean started;
	private Channel channel;
	EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
	public static ConcurrentMap<Long, BlockingQueue<Response>> responseMap = new ConcurrentHashMap<Long, BlockingQueue<Response>>();

	public RpcClient(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}

	public void init() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class)
				.group(eventLoopGroup)
				.handler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
								.addLast(new ResponseCodec())
								.addLast(new RpcClientHandler())
								.addLast(new RequestCodec())
						;
					}
				});
		try {
			ChannelFuture f = bootstrap.connect(serverIp, port).sync();
			this.channel = f.channel();
		} catch (InterruptedException e) {
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
		this.channel.writeAndFlush(request);
		BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<Response>(1);
		responseMap.put(request.getRequestId(), blockingQueue);
		try {
			return blockingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			responseMap.remove(request.getRequestId());
		}
	}

	public <T> T newProxy(final Class<T> serviceInterface) {
		Object o = Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return sendMessage(serviceInterface, method, args).getResponse();
			}
		});
		return (T) o;
	}

	public void destroy() {
		try {
			this.channel.close().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public void notifyEvent(NotifyEvent notifyEvent) {

	}

}
