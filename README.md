# simple-rpc

[![Build Status](https://travis-ci.org/liuzhengyang/simple-rpc.svg?branch=master)](https://travis-ci.org/liuzhengyang/simple-rpc)

RPC BASED ON NETTY
Running in some business online. 

# 内部结构
* IO netty
* Serialize with protostuff, kryo
* Transparent service discovery and connection management

![rpc-architecture](http://oek9m2h2f.bkt.clouddn.com/rpc.png)

# 现有功能
* 基本的客户端、服务端交互
* 提供代理实现接口
* spring 集成, xml配置和Java Config配置方式
* 服务发布订阅 DONE
* 断线重连 DONE

# RoadMap
* 服务心跳检测
* 连接池
* 服务注册发布功能
* 服务管理、监控
* 服务调用日志链路跟踪
* 集成swagger功能,提供文档、测试、客户端生成

# 背景简介
RPC(Remote Procedure Call) 远程服务调用是现在常用的技术，用于多个服务间的互相调用。代码实现示例[simple-rpc](https://github.com/liuzhengyang/simple-rpc)。至于为什么要拆成多个服务，有各种各样的解释和原因，例如解耦、独立发布部署等好处。拆分成服务之后大家各自管理自己的数据和服务，经常会有需要别人数据和服务的需求，不能像整个一体(mo)应用时可以直接获取方法调用，需要通过网络传输调用其他机器上的服务，这样的跨网络、进程通信手写起来非常繁琐易出错。所以出现了很多RPC框架，RPC框架的目标是让我们就想调用本地方法一样调用远程服务并且在性能、易用性等方面有一定需求。并且其他服务可能和自己使用的编程语言不相同，这时就有跨语言调用的情况。常见的RPC框架有thrift、grpc、dubbo等。
<!-- more -->

# RPC原理浅析
考虑一个精简的核心RPC所需要的模块。首先需要有处理网络连接通讯的模块，负责连接建立、管理和消息的传输。其次需要有编解码的模块，因为网络通讯都是传输的字节码，需要将我们使用的对象序列化和反序列化。剩下的就是客户端和服务器端的部分，服务器端暴露要开放的服务接口，客户调用服务接口的一个代理实现，这个代理实现负责收集数据、编码并传输给服务器然后等待结果返回。所以一个调用栈如下

Client invoke => message object => encode to bytes => tranport through net => server decode byte to message object => server service handle message => encode handle result => write back through network
![rpc-architecture](http://oek9m2h2f.bkt.clouddn.com/rpc.png)

# 序列化和反序列化

在应用中，如Java程序都是使用Java对象进行操作，最终传输到另一个台机器上，需要通过网络传输，但是网络传输只识别字节流，所以需要在应用数据和字节码进行转换的工具，一般讲这个转换过程称为编解码或序列化与反序列化。编码(Encode)或序列化(Serialize)的过程指从应用对象转化到字节流的过程，对应的工具也叫编码器(Encoder)，具体编码成什么样的字节流是由对应的编码算法、工具决定的。反过来，由字节流转换为应用对象的过程叫做解码或反序列化。常用的编码工具有protobuf、kryo、Java自带的序列化和反序列化、thrift序列化等。再者我们可以将对象转换成json、xml格式字符串，然后将字符串通过字符编码，如UTF-8等编码方式进行编解码。选择序列化工具时，需要考虑是否有跨语言支持、序列化后的数据大小、性能等因素。[序列化比较](https://github.com/eishay/jvm-serializers/wiki)

# IO传输

## TCP粘包、拆包
tcp传输过程中的任何一个节点都可能会将数据包拆分或合并，最终保证的是数据到达终点的顺序是一致的。由于TCP只关心字节数组流，并不知晓上层的数据格式。所以需要应用层来处理数据是否完整的问题，一般在数据协议上会采用一个字段来表示数据的长度，知道了消息的长度，就可以解决粘包的问题。对于拆包问题，当读到的数据长度比数据长度小时，要继续等待数据。Netty中提供了`LengthFieldBasedFrameDecoder`这个类帮助我们简化粘包、拆包问题，如我们定义协议为表示数据长度的4字节 + 数据，数据长度不包括长度字段本身，假设传输数据"0101"会被转化为 0x 00 00 00 04 01 01。
解码时,`LengthFieldBasedFrameDecoder`中的decode方法中的
```
int frameLengthInt = (int) frameLength;
        if (in.readableBytes() < frameLengthInt) {
            return null;
        }
```
会返回null,数据会在`ByteToMessageDecoder`中累积，直到数据累积充足，解码后返回对象，由后续的Handler处理。

# 服务的注册发布和监听
类似于域名访问的问题，我们无需记住一个http服务后的服务器是哪些，它们的变更对我们都是透明的。对应RPC服务，经常需要使用集群来保证服务的稳定性和共同提高系统的性能。为此需要提供一个注册中心，当服务器启动时进行服务注册，服务器宕机时注册中心能够检测到并将其从服务注册中心删除。客户端要访问一个服务时先到注册中心获取服务列表，然后建立连接进行访问，并且当服务列表发生变化时会收到通知。
![注册发布](http://oek9m2h2f.bkt.clouddn.com/RpcPubSub.png)


# 消息协议
当前采用简单的在消息体前加上4byte的消息长度值

## 使用示例
// 需要先启动一个zookeeper作为服务注册发现中心
```
// 服务接口
public interface IHello {
`
    String say(String hello);

    int sum(int a, int b);
    int sum(Integer a, Integer b);
}
// 服务实现
public class HelloImpl implements IHello {
    public String say(String hello) {
        return "return " + hello;
    }

    public int sum(int a, int b) {
        return a + b;
    }

    public int sum(Integer a, Integer b) {
        return a + b * 3;
    }

}

// 客户端代码
// beanJavaConfig方式
@Bean
	public CountService countService() {
		RpcClientWithLB rpcClientWithLB = new RpcClientWithLB("fyes-counter");
		rpcClientWithLB.setZkConn("127.0.0.1:2181");
		rpcClientWithLB.init();
		CountService countService = rpcClientWithLB.newProxy(CountService.class);
		return countService;
	}
	
// 服务端发布
// xml配置方式
<bean class="com.github.liuzhengyang.simplerpc.ServerFactoryBean" init-method="start">
        <property name="serviceInterface" value="com.test.liuzhengyang.CountService"/>
        <property name="port" value="8888"/>
        <property name="serviceName" value="fyes-counter"/>
        <property name="serviceImpl" ref="countServiceImpl"/>
        <property name="zkConn" value="127.0.0.1:2181"/>

```