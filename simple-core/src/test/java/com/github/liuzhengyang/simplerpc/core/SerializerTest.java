package com.github.liuzhengyang.simplerpc.core;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class SerializerTest {
	static class Person {
		private long createTime;
		private Date updated;
		private String name;
		private int age;
		private Class<?> clazz;

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

		public Date getUpdated() {
			return updated;
		}

		public void setUpdated(Date updated) {
			this.updated = updated;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Person{");
			sb.append("createTime=").append(createTime);
			sb.append(", updated=").append(updated);
			sb.append(", name='").append(name).append('\'');
			sb.append(", age=").append(age);
			sb.append(", clazz=").append(clazz);
			sb.append('}');
			return sb.toString();
		}
	}
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