package com.github.liuzhengyang.simplerpc.core;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-07
 */
public interface IRpcClient {
	void init();
	void destroy();
	void notifyEvent(NotifyEvent notifyEvent);
}
