package com.github.liuzhengyang.simplerpc.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-07
 */
public class ProtostuffSerializer {
	public byte[] serialize(Object obj) {
		RuntimeSchema schema = RuntimeSchema.createFrom(obj.getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
	}

	public <T> T deserialize(Class<T> clazz, byte[] bytes) {
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
