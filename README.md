# rpc-simple-impl

`rpc-simple-impl`是RPC远程过程调用的简单Java实现，目的是为了探究常用的RPC远程调用框架的基本工作原理。

`rpc-simple-impl`使用Zookeeper协调服务作为服务注册与发现的信息载体，使用BIO进行消费者与提供者之间的信息沟通，项目包含三个子模块：

- rpc-interface：服务的抽象接口。
- rpc-service：服务的提供者，模拟分布式环境中service节点。
- rpc-web：服务的消费者，模拟分布式环境中Web入口节点，该项目对外提供HTTP服务，内部的处理过程通过RPC调用`rpc-service`的方法完成。

## rpc-interface
内部拥有`UserService`接口，其中定义了`login`方法。

## rpc-service
服务的提供者，内部有`UserService`接口的实现类`UserServiceImpl`模拟完成登录操作。

启动入口是`RpcServiceApplication.main`。服务启动时会连接Zookeeper注册当前节点`ip
:port`，然后会开启指定端口监听RPC调用请求，RPC调用请求最终都会交由`ServiceExecutor`处理。

## rpc-web
服务的消费者，也是对外提供HTTP服务的项目。项目启动时`cn.tjd.rpcweb.listener.InitListener::contextInitialized`会被触发，它会连接Zookeeper获取`服务提供者`注册的`ip:port`信息，然后缓存在服务器中。

`UserController.login`是对外提供的登录接口，内部会在初始化后通过`RemoteProxy.getInstance()`获取`UserService`的远程调用代理类，代理类中会隐藏网络请求细节。