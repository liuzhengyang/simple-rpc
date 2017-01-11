package com.github.liuzhengyang.simplerpc.serializer;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.liuzhengyang.simplerpc.api.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-07
 */
public class KryoSerializer implements Serializer {
	public byte[] serialize(Object obj) {
		Kryo kryo = new Kryo();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1024);
		Output output = new Output(byteOutputStream);
		kryo.writeClassAndObject(output, obj);
		output.close();
		return byteOutputStream.toByteArray();
	}

	public <T> T deserialize(byte[] bytes) {
		Kryo kryo = new Kryo();
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
		Input input = new Input(byteInputStream);
		input.close();
		return (T) kryo.readClassAndObject(input);
	}
}
