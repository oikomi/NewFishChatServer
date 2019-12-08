package org.miaohong.newfishchatserver.core.lb.strategy;

import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.ServiceInstance;

public interface ServiceStrategy<T> {

    ServiceInstance<T> getInstance();

    NettyClientHandler getNettyClientHandler(String serverAddr);
}
