package com.github.liuzhengyang.simplerpc.core.proxy;

import com.github.liuzhengyang.simplerpc.core.transport.Client;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-23
 */
public interface ClientProxy {
	public <T> T proxyInterface(Client client, final Class<T> serviceInterface);
}
