package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.lb.strategy.ServiceStrategy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerBootstrap<T> extends AbstractConsumerBootstrap<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerBootstrap.class);

    private ServiceStrategy serviceStrategy = ExtensionLoader.getExtensionLoader(ServiceStrategy.class).
            getExtension(ServiceStrategy.class, this.consumerConfig.getStrategy());

    public ConsumerBootstrap(ConsumerConfig<T> consumerConfig) {
        super(consumerConfig);
        startRegister();
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
    public void handleError(Exception e) {
        LOG.error(e.getMessage(), e);
    }

    @Override
    public void destroy() {
        if (register != null) {
            register.unSubscribe();
        }
    }

    @Override
    public void destroy(DestroyHook hook) {

    }

}
