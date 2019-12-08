package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.lb.strategy.RandomStrategy;
import org.miaohong.newfishchatserver.core.lb.strategy.ServiceStrategy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.registry.AbstractRegister;
import org.miaohong.newfishchatserver.core.rpc.registry.RegisterRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerBootstrap<T> extends AbstractConsumerBootstrap<T> implements RegisterRole {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerBootstrap.class);

    private ServiceStrategy serviceStrategy = new RandomStrategy();

    public ConsumerBootstrap(ConsumerConfig<T> consumerConfig, AbstractRegister register) {
        super(consumerConfig, register);

        register.start(this);
        register.subscribe(consumerConfig);
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

        Preconditions.checkState(checkProxy(), "rpc client proxy must be jdk or bytebuddy");
        Preconditions.checkNotNull(proxyFactory);
        proxyInstance = (T) proxyFactory.getProxy(consumerConfig.getProxyClass(), serviceStrategy);

        return proxyInstance;
    }

    @Override
    public void handleError(Exception exception) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void destroy(DestroyHook hook) {

    }

}
