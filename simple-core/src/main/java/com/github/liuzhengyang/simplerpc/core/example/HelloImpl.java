package com.github.liuzhengyang.simplerpc.core.example;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class HelloImpl implements IHello{
	public String say(String hello) {
		return "return " + hello;
	}
}
