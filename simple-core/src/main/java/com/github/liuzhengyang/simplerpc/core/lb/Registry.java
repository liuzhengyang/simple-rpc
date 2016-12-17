package com.github.liuzhengyang.simplerpc.core.lb;

import com.github.liuzhengyang.simplerpc.core.lb.Endpoint;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public interface Registry {
	void registerService(String service, Endpoint endpoint);

	List<Endpoint> getList(String service);

}
