package com.github.liuzhengyang.simplerpc.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-16
 */
public class KryoSerializer {
	public static byte[] serialize(Object obj) {
		Kryo kryo = new Kryo();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1024);
		Output output = new Output(byteOutputStream);
		kryo.writeObject(output, obj);
		output.close();
		return byteOutputStream.toByteArray();
	}

	public static byte[] serializeObjectAndClass(Object obj) {
		Kryo kryo = new Kryo();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteOutputStream);
		kryo.writeClassAndObject(output, obj);
		output.close();
		return byteOutputStream.toByteArray();
	}


	public static <T> T deserialize(Class<T> clazz, byte[] bytes) {
		Kryo kryo = new Kryo();
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
		Input input = new Input(byteInputStream);
		input.close();
		return kryo.readObject(input, clazz);
	}

	public static <T> T deserialize(byte[] bytes) {
		Kryo kryo = new Kryo();
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
		Input input = new Input(byteInputStream);
		return (T) kryo.readClassAndObject(input);
	}
}
