package com.github.liuzhengyang.simpleadmin.controller;

import com.github.liuzhengyang.simpleadmin.mode.ServiceModel;
import com.github.liuzhengyang.simpleadmin.mode.ServiceProvider;
import com.google.common.base.Splitter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-24
 */
@RequestMapping("/admin")
@Controller
public class AdminController {

	private static final String ZK_PATH_PREFIX = "/simplerpc/services";

	private CuratorFramework curatorFramework;

	@PostConstruct
	public void init() {
		curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();
	}

	@RequestMapping("/index")
	public String index(Model model) throws Exception {
		List<String> services = curatorFramework.getChildren().forPath(ZK_PATH_PREFIX);


		final List<ServiceModel> serviceModels = new ArrayList<ServiceModel>();
		if (!CollectionUtils.isEmpty(services)) {
			for (String serviceName : services) {
				ServiceModel serviceModel = new ServiceModel();
				serviceModel.setServiceName(serviceName);
				List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
				List<String> serverPayLoadList = curatorFramework.getChildren().forPath(ZK_PATH_PREFIX + "/" + serviceName);
				if (!CollectionUtils.isEmpty(serverPayLoadList)) {
					for (String serverPayLoad : serverPayLoadList) {
						ServiceProvider serviceProvider = new ServiceProvider();
						List<String> serviceProviderPayLoadTokens = Splitter.on(":").splitToList(serverPayLoad);
						serviceProvider.setIp(serviceProviderPayLoadTokens.get(0));
						serviceProvider.setPort(serviceProviderPayLoadTokens.get(1));
						serviceProviders.add(serviceProvider);
					}
				}
				serviceModel.setServiceProviders(serviceProviders);

				serviceModels.add(serviceModel);
			}
		}
		model.addAttribute("services", serviceModels);
		return "index";
	}

	@RequestMapping("/index/{service}")
	public String serviceDetail(@PathVariable("service") String service, Model model) throws Exception {
		List<String> servers = curatorFramework.getChildren().forPath(ZK_PATH_PREFIX + "/" + service);
		model.addAttribute("servers", servers);
		return "detail";
	}
}
