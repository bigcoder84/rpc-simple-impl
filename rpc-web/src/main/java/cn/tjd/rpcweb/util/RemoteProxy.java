package cn.tjd.rpcweb.util;

import cn.tjd.rpcweb.zk.LoadBalance;
import cn.tjd.rpcweb.zk.RandomLoadBalance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @Auther: TJD
 * @Date: 2020-06-28
 * @DESCRIPTION:
 **/
public class RemoteProxy {
    public final static LoadBalance LOAD_BALANCE = new RandomLoadBalance();

    public static <T> T getInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(RemoteProxy.class.getClassLoader(), new Class[]{clazz},
                new RemoteProcessCallInvocatoinHandler(clazz));
    }

    private static class RemoteProcessCallInvocatoinHandler implements InvocationHandler {
        private Class interfaceClass;

        public RemoteProcessCallInvocatoinHandler(Class interfaceClass) {
            this.interfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String service = RemoteProxy.LOAD_BALANCE.getService();
            if (service == null) {
                throw new RuntimeException("没有可用的RPC远程服务");
            }
            System.out.println(service+"节点提供服务");
            String[] split = service.split(":");
            String ip = split[0];
            Integer port = Integer.valueOf(split[1]);
            Socket socket = new Socket(ip, port);
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                //输出接口名称
                output.writeUTF(interfaceClass.getName());
                //输出方法名称
                output.writeUTF(method.getName());
                //获取参数列表类型
                output.writeObject(method.getParameterTypes());
                //获取参数
                output.writeObject(args);
                output.flush();
                input = new ObjectInputStream(socket.getInputStream());
                //获取执行结果
                Object result = input.readObject();
                return result;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
    }
}
