package cn.tjd.rpcweb.zk;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Auther: TJD
 * @Date: 2020-06-18
 * @DESCRIPTION:
 **/
public abstract class LoadBalance {
    static final List<String> SERVICE_LIST = new LinkedList<>();

    static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();


    public static void updateServiceList(List<String> serviceList){
        LOCK.writeLock().lock();
        try {
            //删除所有元素
            SERVICE_LIST.removeIf(s -> true);
            SERVICE_LIST.addAll(serviceList);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public abstract String getService();
}
