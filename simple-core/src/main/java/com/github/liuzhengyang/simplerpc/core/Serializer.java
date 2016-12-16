package com.github.liuzhengyang.simplerpc.core;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-15
 */
public class Serializer {
	public static byte[] serialize(Object obj){
		RuntimeSchema schema = RuntimeSchema.createFrom(obj.getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
	}


	public static <T> T deserialize(Class<T> clazz, byte[] bytes) {
		try {
			T t = clazz.newInstance();
			RuntimeSchema schema = RuntimeSchema.createFrom(clazz);
			ProtostuffIOUtil.mergeFrom(bytes, t, schema);

			return t;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
