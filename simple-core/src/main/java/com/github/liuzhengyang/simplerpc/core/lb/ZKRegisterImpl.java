package com.github.liuzhengyang.simplerpc.core.lb;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
// TODO curator discovery 是否会自动在机器停止后删除节点
public class ZKRegisterImpl implements Registry{
	private CuratorFramework curatorFramework ;
	private ServiceDiscovery<com.github.liuzhengyang.simplerpc.core.lb.Endpoint> serviceDiscovery;
	public ZKRegisterImpl() {
		JsonInstanceSerializer<Endpoint> serializer = new JsonInstanceSerializer<Endpoint>(Endpoint.class);
		curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 10));
		curatorFramework.start();
		serviceDiscovery = ServiceDiscoveryBuilder.builder(Endpoint.class).client(curatorFramework).serializer(serializer)
				.basePath("services").build();
	}
	public void registerService(String service, Endpoint endpoint) {
		try {
			ServiceInstance<Endpoint> serviceInstance =  ServiceInstance.<Endpoint>builder().name(service)
					.port(endpoint.getPort()).payload(endpoint).build();
			serviceDiscovery.registerService(serviceInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Endpoint> getList(String service) {
		try {
			Collection<ServiceInstance<Endpoint>> serviceInstances = serviceDiscovery.queryForInstances(service);
			List<Endpoint> endpointList = new ArrayList<Endpoint>();
			if (CollectionUtils.isNotEmpty(serviceInstances)) {
				for (ServiceInstance<Endpoint> serviceInstance: serviceInstances) {
					endpointList.add(serviceInstance.getPayload());
				}
			}
			return endpointList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
