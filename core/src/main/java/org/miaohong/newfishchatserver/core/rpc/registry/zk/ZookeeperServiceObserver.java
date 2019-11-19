package org.miaohong.newfishchatserver.core.rpc.registry.zk;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ZookeeperServiceObserver implements ServiceObserver {

    private ConcurrentMap<ConsumerConfig, List<ZookeeperServiceListener>> serviceListenerMap = new ConcurrentHashMap<>();


    @Override
    public void addService(ConsumerConfig config, String servicePath, ChildData data, List<ChildData> currentData) {

    }

    @Override
    public void removeService(ConsumerConfig config, String servicePath, ChildData data, List<ChildData> currentData) {

    }

    @Override
    public void updateService(ConsumerConfig config, String servicePath, ChildData data, List<ChildData> currentData) {

    }

    @Override
    public void addListener(ServiceListener listener) {

    }

    @Override
    public void notifyListeners() {

    }

}
