package com.github.liuzhengyang.simpleadmin.mode;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-28
 */
public class ServiceModel {
	private String serviceName;
	private String startTime;
	private List<ServiceProvider> serviceProviders;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public List<ServiceProvider> getServiceProviders() {
		return serviceProviders;
	}

	public void setServiceProviders(List<ServiceProvider> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}
}
