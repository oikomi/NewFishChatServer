package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;

public class ConsumerBootstrap<T> extends AbstractConsumerBootstrap<T> {

    public ConsumerBootstrap(ConsumerConfig<T> consumerConfig, Register register) {
        super(consumerConfig, register);
    }

    private boolean checkProxy() {
        return ProxyConstants.PROXY_JDK.equals(consumerConfig.getProxy())
                || ProxyConstants.PROXY_BYTEBUDDY.equals(consumerConfig.getProxy());
    }

    public T refer() {
        Preconditions.checkNotNull(register);

        if (proxyInstance != null) {
            return proxyInstance;
        }

        register.start();
        register.subscribe(consumerConfig);

        Preconditions.checkState(checkProxy(), "rpc client proxy must be jdk or bytebuddy");
        Preconditions.checkNotNull(proxyFactory);
        proxyInstance = (T) proxyFactory.getProxy(consumerConfig.getProxyClass());

        return proxyInstance;
    }
}
