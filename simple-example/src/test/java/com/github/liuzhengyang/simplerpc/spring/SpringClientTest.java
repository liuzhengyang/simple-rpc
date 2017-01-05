package com.github.liuzhengyang.simplerpc.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-05
 */
@SpringBootApplication
@ImportResource(locations = "application.xml")
public class SpringClientTest {
	public static void main(String[] args) {
		SpringApplication.run(SpringClientTest.class);
	}
}
