package cn.tjd.rpcweb.zk;

import java.util.List;
import java.util.Random;

/**
 * 根据随机数选择服务提供者
 * @Auther: TJD
 * @Date: 2020-06-18
 * @DESCRIPTION:
 **/
public class RandomLoadBalance extends LoadBalance {
    @Override
    public String getService() {
        List<String> serviceList = LoadBalance.SERVICE_LIST;
        if (serviceList.size()>0) {
            int index = new Random().nextInt(serviceList.size());
            return serviceList.get(index);
        }
        return null;
    }
}
