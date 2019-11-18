package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;

public class ConsumerBootstrap<T> extends AbstractConsumerBootstrap<T> {

    public ConsumerBootstrap(ConsumerConfig<T> config) {
        super(config);
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
        proxyInstance = (T) proxyFactory.getProxy(config.getProxyClass());

        return proxyInstance;
    }
}
