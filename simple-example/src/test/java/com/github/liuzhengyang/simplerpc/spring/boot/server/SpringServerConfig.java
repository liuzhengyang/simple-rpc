package com.github.liuzhengyang.simplerpc.spring.boot.server;

import com.github.liuzhengyang.simplerpc.ServerFactoryBean;
import com.github.liuzhengyang.simplerpc.example.HelloImpl;
import com.github.liuzhengyang.simplerpc.example.IHello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-05
 */
@SpringBootApplication
public class SpringServerConfig {
	@Bean
	public IHello hello() {
		return new HelloImpl();
	}

	@Bean
	public ServerFactoryBean serverFactoryBean() {
		final ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
		serverFactoryBean.setPort(9090);
		serverFactoryBean.setServiceInterface(IHello.class);
		serverFactoryBean.setServiceImpl(hello());
		new Thread(new Runnable() {
			public void run() {
				try {
					serverFactoryBean.getObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "RpcServer").start();
		return serverFactoryBean;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringServerConfig.class);
	}
}
