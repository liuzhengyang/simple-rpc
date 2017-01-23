package com.github.liuzhengyang.simplerpc.core;

import com.github.liuzhengyang.simplerpc.core.pool.ConnectionObjectFactory;
import io.netty.channel.Channel;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-23
 */
class ChannelWrapper {
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
