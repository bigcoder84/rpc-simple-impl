package cn.tjd.rpcservice;


import cn.tjd.rpcservice.executor.ServiceExecutor;
import cn.tjd.rpcservice.service.UserServiceImpl;
import cn.tjd.rpcservice.zk.ServiceRegister;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RpcServiceApplication {

    /**
     * RPC远程调用的端口
     */
    private static final Integer port = 8888;
    /**
     * Zookeeper集群地址
     */
    private static final String zkCluster = "192.168.1.106:2181,192.168.1.107:2181,192.168.1.105:2181";
    /**
     * Service实现类与其实现的接口名的对应关系
     */
    public static final Map<String, Class> SERVICES_CLASS_CACHE = new HashMap<>();

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException {
        //缓存需要提供服务的实现类与接口的对应关系
        mappingInterfaceToClass();
        //注册服务地址
        registerService();
        //初始化远程服务
        initRemoteProcessCall();
    }

    /**
     * 注册服务地址
     */
    private static void registerService() {
        try {
            //获取当前节点的IP
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            //注册服务
            ServiceRegister.register(zkCluster,hostAddress, port.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化远程服务
     * @throws IOException
     */
    private static void initRemoteProcessCall() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("处理了一次远程请求");
                new Thread(new ServiceExecutor(socket)).start();
            }
        } finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
        }
    }

    /**
     * 缓存需要提供服务的实现类与接口的对应关系
     */
    private static void mappingInterfaceToClass() {
        mappingInterfaceToClass(UserServiceImpl.class);
    }

    /**
     * 将指定服务实现类注册进缓存
     *
     * @param clazz
     */
    private static void mappingInterfaceToClass(Class clazz) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class anInterface : interfaces) {
            SERVICES_CLASS_CACHE.put(anInterface.getName(), clazz);
        }
    }


}
