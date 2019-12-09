package org.miaohong.newfishchatserver.core.rpc.client.proxy.jdk;


import org.miaohong.newfishchatserver.annotations.SpiMeta;
import org.miaohong.newfishchatserver.core.lb.strategy.ServiceStrategy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

@SpiMeta(name = ProxyConstants.PROXY_JDK)
public class JdkProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, ServiceStrategy serviceStrategy) {
        return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new JDKInvocationHandler<>(serviceStrategy));
    }
}
