package org.miaohong.newfishchatserver.core.rpc.client;

import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;

public abstract class AbstractConsumerBootstrap<T> {

    protected T proxyInstance;
    protected ConsumerConfig config;
    protected ProxyFactory proxyFactory;

    public AbstractConsumerBootstrap(ConsumerConfig<T> config) {
        this.config = config;
        proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).
                getExtension(ProxyFactory.class, this.config.getProxy());
    }

}
