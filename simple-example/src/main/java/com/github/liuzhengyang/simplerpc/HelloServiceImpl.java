package com.github.liuzhengyang.simplerpc;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class HelloServiceImpl implements HelloService {
	public String hello(int year, String name) {
		return "Hello " + name + " At " + year;
	}
}
