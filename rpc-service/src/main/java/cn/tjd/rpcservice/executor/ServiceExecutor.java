package cn.tjd.rpcservice.executor;

import cn.tjd.rpcservice.RpcServiceApplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Auther: TJD
 * @Date: 2020-06-28
 * @DESCRIPTION:
 **/
public class ServiceExecutor implements Runnable {

    private Socket socket;

    public ServiceExecutor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            //获取接口名称
            String interfaceName = input.readUTF();
            //获取方法名称
            String methodName = input.readUTF();
            //获取参数列表类型
            Class[] parameterTypes = (Class[]) input.readObject();
            //获取参数
            Object[] arguments = (Object[]) input.readObject();
            //根据获取到的信息获取实际提供服务的对象的字节码对象
            Class clazz = RpcServiceApplication.SERVICES_CLASS_CACHE.get(interfaceName);
            //获取并调用目标方法，从而获取返回结果
            Method method = clazz.getMethod(methodName, parameterTypes);
            Object result = method.invoke(clazz.newInstance(), arguments);
            //将返回结果返回给客户端
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(result);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
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
    }
}
