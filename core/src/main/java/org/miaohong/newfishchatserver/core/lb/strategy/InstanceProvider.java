package org.miaohong.newfishchatserver.core.lb.strategy;

import org.miaohong.newfishchatserver.core.rpc.register.serializer.ServiceInstance;

import java.util.List;

public interface InstanceProvider {

    List<ServiceInstance> getInstances();

    List<ServiceInstance> getInstances(int timeout);
}
