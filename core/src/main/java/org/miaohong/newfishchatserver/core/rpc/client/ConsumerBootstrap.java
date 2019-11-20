package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConsumerBootstrap<T> extends AbstractConsumerBootstrap<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerBootstrap.class);

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
        List<String> servers = register.subscribe(consumerConfig);

        LOG.info(String.valueOf(servers));

        Preconditions.checkState(checkProxy(), "rpc client proxy must be jdk or bytebuddy");
        Preconditions.checkNotNull(proxyFactory);
        proxyInstance = (T) proxyFactory.getProxy(consumerConfig.getProxyClass());

        return proxyInstance;
    }
}
