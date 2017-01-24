package com.github.liuzhengyang.simpleadmin.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
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

	private CuratorFramework curatorFramework;

	@PostConstruct
	public void init() {
		curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();
	}

	@RequestMapping("/index")
	public String index(Model model) throws Exception {
		List<String> services = curatorFramework.getChildren().forPath("/simplerpc/services");
		model.addAttribute("services", services);
		return "index";
	}

	@RequestMapping("/index/{service}")
	public String serviceDetail(@PathVariable("service") String service, Model model) throws Exception {
		List<String> servers = curatorFramework.getChildren().forPath("/simplerpc/services/" + service);
		model.addAttribute("servers", servers);
		return "detail";
	}
}
