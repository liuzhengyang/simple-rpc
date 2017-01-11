package com.github.liuzhengyang.simplerpc.api;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-07
 */
public interface Serializer {
	public byte[] serialize(Object obj);
	public <T> T deserialize(byte[] bytes);
}
