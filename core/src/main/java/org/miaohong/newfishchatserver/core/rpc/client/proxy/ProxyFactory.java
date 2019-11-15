package org.miaohong.newfishchatserver.core.rpc.client.proxy;


public interface ProxyFactory {
    <T> T getProxy(Class<T> clz);
}
