package com.github.liuzhengyang.simplerpc.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class KryoSerializer {
	public static byte[] serialize(Object obj){
		Kryo kryo = new Kryo();
		ByteOutputStream byteOutputStream = new ByteOutputStream();
		Output output = new Output(byteOutputStream);
		kryo.writeObject(output, obj);
		return byteOutputStream.getBytes();
	}


	public static <T> T deserialize(Class<T> clazz, byte[] bytes) {
		Kryo kryo = new Kryo();
		ByteInputStream byteInputStream = new ByteInputStream(bytes, bytes.length);
		Input input = new Input(byteInputStream);
		return kryo.readObject(input, clazz);
	}
}
