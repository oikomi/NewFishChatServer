package org.miaohong.newfishchatserver.core.rpc.registry.zk;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;

import java.util.List;

public interface ServiceObserver {

    void addService(ConsumerConfig config, String servicePath, ChildData data, List<ChildData> currentData);

    void removeService(ConsumerConfig config, String servicePath, ChildData data, List<ChildData> currentData);

    void updateService(ConsumerConfig config, String servicePath, ChildData data, List<ChildData> currentData);

    void addListener(ServiceListener listener);

    void notifyListeners();
}
