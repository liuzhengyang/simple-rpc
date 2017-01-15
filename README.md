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