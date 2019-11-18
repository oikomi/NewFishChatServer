package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;

public class ConsumerBootstrap<T> {

    private T proxyInstance;
    private ConsumerConfig<T> config;
    private ProxyFactory proxyFactory;

    public ConsumerBootstrap(ConsumerConfig<T> config) {
        this.config = config;
        proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).
                getExtension(ProxyFactory.class, this.config.getProxy());
    }

    private boolean checkProxy() {
        return ProxyConstants.PROXY_JDK.equals(config.getProxy())
                || ProxyConstants.PROXY_BYTEBUDDY.equals(config.getProxy());
    }

    public T refer() {
        if (proxyInstance != null) {
            return proxyInstance;
        }

        Preconditions.checkState(checkProxy(), "rpc client proxy must be jdk or bytebuddy");
        Preconditions.checkNotNull(proxyFactory);
        proxyInstance = proxyFactory.getProxy(config.getProxyClass());

        return proxyInstance;
    }
}
