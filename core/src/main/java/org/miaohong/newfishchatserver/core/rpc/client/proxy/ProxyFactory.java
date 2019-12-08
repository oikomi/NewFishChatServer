package org.miaohong.newfishchatserver.core.rpc.client.proxy;


import org.miaohong.newfishchatserver.core.lb.strategy.ServiceStrategy;

public interface ProxyFactory {
    <T> T getProxy(Class<T> clz, ServiceStrategy serviceStrategy);
}
