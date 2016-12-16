package com.github.liuzhengyang.simplerpc.example;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public interface IHello {

	String say(String hello);

	int sum(int a, int b);
	int sum(Integer a, Integer b);
}
