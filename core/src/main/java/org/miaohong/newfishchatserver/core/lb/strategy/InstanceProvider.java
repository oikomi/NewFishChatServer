package org.miaohong.newfishchatserver.core.lb.strategy;

import org.miaohong.newfishchatserver.core.rpc.registry.serializer.ServiceInstance;

import java.util.List;

public interface InstanceProvider<T> {

    List<ServiceInstance<T>> getInstances();
}
