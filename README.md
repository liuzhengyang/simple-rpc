# simple-rpc

RCP BASED ON NETTY

## 使用示例
```
// 服务接口
public interface IHello {

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