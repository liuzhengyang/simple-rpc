# simple-rpc

RPC BASED ON NETTY

# 内部结构
* IO netty
* Serialize protostuff, kryo
* Use Zookeeper For Service Discovery

# 现有功能
* 基本的客户端、服务端交互
* 提供代理实现接口

# RoadMap
* 服务发布订阅 DONE
* 服务心跳检测, 断线重连
* spring 结合
* 连接池
* 服务注册发布功能
* 服务管理、监控
* 服务调用日志链路跟踪
* 集成swagger功能,提供文档、测试、客户端生成


# 消息协议
当前采用简单的在消息体前加上4byte的消息长度值

## 使用示例
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

// 服务端代码
public class RpcServerTest {
    @Test
    public void init() throws Exception {
        RpcServer rpcServer = new RpcServer(8090, new HelloImpl());
        rpcServer.init();
    }

}
// 客户端调用
public class RpcClientTest {
    @Test
    public void init() throws Exception {
        RpcClient rpcClient = new RpcClient("127.0.0.1", 8090);
        rpcClient.init();
        IHello ihello = rpcClient.newProxy(IHello.class);
        String nihaoya = ihello.say("nihaoya");
        System.out.println(nihaoya);
        System.out.println(ihello.sum(2, 4));
        rpcClient.destroy();
    }

}
```