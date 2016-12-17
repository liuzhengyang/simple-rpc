package com.github.liuzhengyang.simplerpc.core.lb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class MapRegistryImpl implements Registry {
	private ConcurrentMap<String, List<Endpoint>> registryMap = new ConcurrentHashMap<String, List<Endpoint>>();
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private Lock readLock = readWriteLock.readLock();
	private Lock writeLock = readWriteLock.writeLock();

	public void registerService(String service, Endpoint endpoint) {
		writeLock.lock();
		try {
			List<Endpoint> endpoints = registryMap.get(service);
			if (endpoints != null) {
				endpoints.add(endpoint);
			} else {
				List<Endpoint> endpointList = new ArrayList<Endpoint>();
				endpointList.add(endpoint);
			}
		} finally {
			writeLock.unlock();
		}
	}

	public void unRegister(String service, Endpoint endpoint) {

	}

	public List<Endpoint> getList(String service) {
		readLock.lock();
		try {
			return registryMap.get(service);
		} finally {
			readLock.unlock();
		}
	}
}
