package org.miaohong.newfishchatserver.core.rpc.client.proxy;


import java.lang.reflect.Proxy;

public class JdkProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz) {
        return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new CallerInvocationHandler<>(clz));
    }
}
