package org.miaohong.newfishchatserver.core.rpc.client;

import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;

public abstract class AbstractConsumerBootstrap<T> {

    protected T proxyInstance;
    protected ConsumerConfig consumerConfig;
    protected ProxyFactory proxyFactory;

    protected Register register;

    public AbstractConsumerBootstrap(ConsumerConfig<T> consumerConfig, Register register) {
        this.consumerConfig = consumerConfig;
        proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).
                getExtension(ProxyFactory.class, this.consumerConfig.getProxy());
        this.register = register;
    }

}
