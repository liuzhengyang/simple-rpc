package com.github.liuzhengyang.simplerpc.core;

import org.junit.Test;

import java.util.Date;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class SerializerTest {
	@Test
	public void serialize() throws Exception {
		Person p = new Person();
		p.setAge(10);
		p.setCreateTime(124124);
		p.setName("nihao");
		p.setUpdated(new Date());
		p.setClazz(String.class);
		byte[] serialize = Serializer.serialize(p);
		System.out.println(serialize);
		Person deserialize = Serializer.deserialize(Person.class, serialize);
		System.out.println(deserialize);
	}

	@Test
	public void deserialize() throws Exception {

	}

}