package com.github.liuzhengyang.simplerpc.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class KryoSerializerTest {
	@Test
	public void serialize() throws Exception {
		Person p = new Person();
		p.setAge(10);
		p.setCreateTime(124124);
		p.setName("nihao");
		p.setUpdated(new Date());
		p.setClazz(String.class);
		byte[] serialize = KryoSerializer.serializeObjectAndClass(p);

		System.out.println(serialize.length);

		Person deserialize = KryoSerializer.deserialize(serialize);
		System.out.println(deserialize);
	}

	@Test
	public void deserialize() throws Exception {
		Person p = new Person();
		p.setAge(10);
		p.setCreateTime(124124);
		p.setName("nihao");
		p.setUpdated(new Date());
		p.setClazz(String.class);
		byte[] serialize = KryoSerializer.serialize(p);

		System.out.println(serialize.length);
		System.out.println(Arrays.toString(serialize));

		Person deserialize = KryoSerializer.deserialize(Person.class, serialize);
		System.out.println(deserialize);
	}

}