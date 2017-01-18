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
* 断线重连 重写
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
tcp传输过程中的任何一个节点都可能会将数据包拆分或合并，最终保证的是数据到达终点的顺序是一致的。由于TCP只关心字节数组流，并不知晓上层的数据格式。所以需要应用层来处理数据是否完整的问题，一般在数据协议上会采用一个字段来表示数据的长度，知道了消息的长度，就可以解决粘包的问题。对于拆包问题，当读到的数据长度比数据长度小时，要继续等待数据。Netty中提供了`LengthFieldBasedFrameDecoder`这个类帮助我们简化粘包、拆包问题，如我们定义协议为表示数据长度的4字节 + 数据，数据长度不包括长度字段本身，假设传输数据"0101"被转化为 0x 00 00 00 04 01 01。可以使用LengthFieldBasedFrameDecoder(0, 4, 0, 4)来进行解码。
解码遇到拆包时,`LengthFieldBasedFrameDecoder`中的decode方法中的
```
int frameLengthInt = (int) frameLength;
        if (in.readableBytes() < frameLengthInt) {
            return null;
        }
```
会返回null,数据会在`ByteToMessageDecoder`中累积，直到数据累积充足，解码后返回对象，由后续的Handler处理。

# 服务的注册发布和监听
类似于域名访问的问题，我们无需记住一个http服务后的服务器是哪些，它们的变更对我们都是透明的。对应RPC服务，经常需要使用集群来保证服务的稳定性和共同提高系统的性能。为此需要提供一个注册中心，当服务器启动时进行服务注册，服务器宕机时注册中心能够检测到并将其从服务注册中心删除。客户端要访问一个服务时先到注册中心获取服务列表，缓存到本地，然后建立连接进行访问，并且当服务列表发生变化时会收到通知并修改本地缓存。
![注册发布](http://oek9m2h2f.bkt.clouddn.com/RpcPubSub.png)

# 服务路由
有注册中心后，调用方调用服务提供者时可以动态的获取调用方的地址等信息进行选择，类似域名的机制。这样增加了一层抽象，避免了写死ip等问题。又一次说明了`Any problem in computer science can be solved by adding another level of indirection`。当有多个服务提供者时，调用方需要在其中选择一个进行调用，常见的调用策略有随机、Round-Robin(轮询)、权重比例等。采取权重方式可以通过服务调用的耗时、异常数量进行动态调用权重，也可以进行人工调整。当我们需要更复杂的控制策略是，可以通过脚本编写策略，并可以动态修改。

# IO调用方式
## nio和bio
bio指的是传统的阻塞io，在Java中使用方式是Socket、ServerSocket、InputStream、OutputStream的组合。当读数据是，如果没有数据可读，该线程会被切换为阻塞状态，直到数据可读等待处理器调度运行，会进行两次上下文切换和两次用户态内核态切换，并且这样一个线程同时只能处理一个连接，线程是比较宝贵的资源，除了创建和销毁线程的开销外，JVM中每个线程都会有1MB左右的栈大小，这样一线程一连接的方式无法应对单机数万的情况，这时的内存消耗和上下文切换的成本都非常高。Nio指non blocking io，即非阻塞io，当数据不可读时会返回一个错误而不是阻塞，在Java中，常用Selector、SocketChannel、ServerSocketChannel、ByteBuffer的组合实现nio服务，在一个Selector上可以监听多个连接的是否可读、可写、可连接等状态，这样一个线程就可以同时处理很多个连接，能够提高系统连接能力。
下面的经典的几张图可以说明bio和nio的区别
![bio](http://oek9m2h2f.bkt.clouddn.com/io:bio)
![nio](http://oek9m2h2f.bkt.clouddn.com/nio.jpg)
## 同步和异步
同步指发出一个请求后是否阻塞并一直等待结果返回，而异步可以在发送请求后先去执行其他任务，在一段时间后再获取结果或通过注册监听器设置回调。在Java中一般是通过Future或者一些Listener来实现异步调用。如ExecutorService.submit()方法返回一个Future，调用Future的get时会阻塞，可以在get时设置超时时间。Guava和Netty中的Future实现可以设置Listener在结果成功或失败时进行回调。

# 实现
下面利用一些好用的框架帮助我们快速的实现一个RPC。 源代码在 *[simple-rpc](https://github.com/liuzhengyang/simple-rpc)*
* netty 负责数据传输部分，netty作为异步事件驱动的高性能IO框架，使用方便久经考验，比手工编写nio代码方便不易出错。
* protostuff 负责序列化和反序列化。google的 protobuf需要编写IDL文件然后生成，好处是能够生成各个语言的代码。但是开发起来很繁琐，使用protostuff免去编写IDL文件并生成的痛苦。


## 请求和返回的抽象
```
@Data
public class Request {
    private long requestId;
    private Class<?> clazz;
    private String method;
    private Class<?>[] parameterTypes;
    private Object[] params;
    private long requestTime;
}
@Data
public class Response {
    private long requestId;
    private Object response;
}


```

## 编解码部分
```
public class RequestCodec extends ByteToMessageCodec<Request>{
    protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
        byte[] bytes = Serializer.serialize(msg);
        int length = bytes.length;
        out.writeInt(length);
        ByteBuf byteBuf = out.writeBytes(bytes);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.readBytes(buffer);
        Request request = Serializer.deserialize(Request.class, buffer);
        out.add(request);
    }
}

public class ResponseCodec extends ByteToMessageCodec<Response>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseCodec.class);

    protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
        LOGGER.info("Encode {}", msg);
        byte[] bytes = Serializer.serialize(msg);
        int length = bytes.length;
        out.writeInt(length);
        ByteBuf byteBuf = out.writeBytes(bytes);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.readBytes(buffer);
        Response response = Serializer.deserialize(Response.class, buffer);
        out.add(response);
        LOGGER.info("Decode Result: {}", response);
    }
}


```


## Client 
```
public class RpcClientHandler extends SimpleChannelInboundHandler<Response>{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        LOGGER.info("Receive {}", msg);
        BlockingQueue<Response> blockingQueue = RpcClient.responseMap.get(msg.getRequestId());
        blockingQueue.put(msg);
    }
}

public class RpcClient {
    private static AtomicLong atomicLong = new AtomicLong();
    private String serverIp;
    private int port;
    private boolean started;
    private Channel channel;
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    public static ConcurrentMap<Long, BlockingQueue<Response>> responseMap = new ConcurrentHashMap<Long, BlockingQueue<Response>>();

    public RpcClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void init() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(eventLoopGroup)
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new ResponseCodec())
                                .addLast(new RpcClientHandler())
                                .addLast(new RequestCodec())
                        ;
                    }
                });
        try {
            ChannelFuture f = bootstrap.connect(serverIp, port).sync();
            this.channel = f.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Response sendMessage(Class<?> clazz, Method method, Object[] args) {
        Request request = new Request();
        request.setRequestId(atomicLong.incrementAndGet());
        request.setMethod(method.getName());
        request.setParams(args);
        request.setClazz(clazz);
        request.setParameterTypes(method.getParameterTypes());
        this.channel.writeAndFlush(request);
        BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<Response>(1);
        responseMap.put(request.getRequestId(), blockingQueue);
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T newProxy(final Class<T> serviceInterface) {
        Object o = Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return sendMessage(serviceInterface, method, args).getResponse();
            }
        });
        return (T) o;
    }

    public void destroy() {
        try {
            this.channel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

}

```

## Server
```
public class RpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String ip;
    private int port;
    private boolean started = false;
    private Channel channel;
    private Object serviceImpl;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public RpcServer(int port, Object serviceImpl) {
        this.port = port;
        this.serviceImpl = serviceImpl;
    }

    public void init() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new RequestCodec())
                                .addLast(new RpcServerHandler(serviceImpl))
                                .addLast(new ResponseCodec())
                        ;
                    }
                });
        try {
            ChannelFuture sync = bootstrap.bind(port).sync();
            LOGGER.info("Server Started At {}", port);
            started = true;
            this.channel = sync.channel();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

public class RpcServerHandler extends SimpleChannelInboundHandler<Request> {
    private Object service;

    public RpcServerHandler(Object serviceImpl) {
        this.service = serviceImpl;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        String methodName = msg.getMethod();
        Object[] params = msg.getParams();
        Class<?>[] parameterTypes = msg.getParameterTypes();
        long requestId = msg.getRequestId();
        Method method = service.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        Object invoke = method.invoke(service, params);
        Response response = new Response();
        response.setRequestId(requestId);
        response.setResponse(invoke);
        ctx.pipeline().writeAndFlush(response);
    }
}

```

## 序列化部分
```
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
```

# SimpleRpc使用示例
*需要先启动一个zookeeper作为服务注册发现中心*

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