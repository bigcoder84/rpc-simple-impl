package cn.tjd.rpcweb.listener;

import cn.tjd.rpcweb.zk.LoadBalance;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * 该类用于在Servlet容器启动时，加载Zookeeper中的服务
 * @Auther: TJD
 * @Date: 2020-06-18
 * @DESCRIPTION:
 **/
public class InitListener implements ServletContextListener {

    private static final String BASE_SERVICES = "/SERVICES";
    private static final String SERVICE_NAME = "/RPC";

    private ZooKeeper zooKeeper;

    private String zkCluster;

    public InitListener(String zkCluster) {
        this.zkCluster = zkCluster;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            zooKeeper = new ZooKeeper(zkCluster, 5000,
                    watchedEvent -> {
                        if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged && watchedEvent.getPath().equals(BASE_SERVICES + SERVICE_NAME)) {
                            //当BASE_SERVICES + SERVICE_NAME子节点发生变动时触发事件，调用updateServices()更新服务列表
                            updateServices();
                        }
                    });
            updateServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateServices(){
        System.out.println("节点信息更新");
        try {
            //服务对应的子节点，将watch参数设置true表示开启下一次监听，zk树的下一次修改仍然会被Watcher监听到
            List<String> childs = zooKeeper.getChildren(BASE_SERVICES + SERVICE_NAME, true);
            List<String> serviceList = new LinkedList<>();
            for (String child : childs) {
                byte[] data = zooKeeper.getData(BASE_SERVICES  + SERVICE_NAME + "/" + child, false, null);
                String host = new String(data, "UTF-8");
                serviceList.add(host);
            }
            //更新本地保存的服务列表
            LoadBalance.updateServiceList(serviceList);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
