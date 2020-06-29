package cn.tjd.rpcservice.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @Auther: TJD
 * @Date: 2020-06-18
 * @DESCRIPTION:
 **/
public class ServiceRegister {

    private static final String BASE_SERVICES = "/SERVICES";
    private static final String SERVICE_NAME = "/RPC";

    public static void register(String zkCluster,String ip, String port) {
        try {
            //连接Zookeeper
            ZooKeeper zooKeeper = new ZooKeeper(zkCluster, 5000, (watchedEvent) -> {});
            //获取根节点
            Stat baseServiceNode = zooKeeper.exists(BASE_SERVICES, false);
            if (baseServiceNode == null) {
                //如果没有则创建持久类型的根节点
                zooKeeper.create(BASE_SERVICES, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //获取根节点下对应服务的节点
            Stat serviceNameNode = zooKeeper.exists(BASE_SERVICES + SERVICE_NAME, false);
            if (serviceNameNode == null) {
                //如果没有则创建持久类型的服务节点
                zooKeeper.create(BASE_SERVICES + SERVICE_NAME, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
            String serviceStr = ip + ":" + port;
            //创建临时顺序节点，值为当前服务对应的ip:port
            //创建临时顺序节点的目的：当节点挂掉的时候，创建的这个节点会自动删除，从而触发客户端的监听事件更新消费者机器上的节点列表
            zooKeeper.create(BASE_SERVICES + SERVICE_NAME + "/child", serviceStr.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
