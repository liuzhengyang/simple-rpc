package com.github.liuzhengyang.simplerpc.spring.boot.client;

import com.github.liuzhengyang.simplerpc.ClientFactoryBean;
import com.github.liuzhengyang.simplerpc.example.IHello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-05
 */
@Configuration
@RestController
@SpringBootApplication
@RequestMapping("/test")
public class SpringConfig {

	@Bean
	public IHello rpcClient() {
		ClientFactoryBean<IHello> clientFactoryBean = new ClientFactoryBean<IHello>();
		clientFactoryBean.setIp("127.0.0.1");
		clientFactoryBean.setPort(9090);
		clientFactoryBean.setServiceInterface(IHello.class);
		return clientFactoryBean.getObject();
	}

	@Resource
	private IHello hello;

	@RequestMapping("/hello")
	public String hello(String say) {
		return hello.say(say);
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, "--server.port=8081");
	}
}
