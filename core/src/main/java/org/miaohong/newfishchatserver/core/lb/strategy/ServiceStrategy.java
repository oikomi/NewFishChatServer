package org.miaohong.newfishchatserver.core.lb.strategy;

import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.rpc.register.serializer.ServiceInstance;

public interface ServiceStrategy {

    ServiceInstance getInstance(int timeout);

    ServiceInstance getInstance();

    NettyClientHandler getNettyClientHandler(String serverAddr);
}
