package org.miaohong.newfishchatserver.core.rpc.client;

import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;
import org.miaohong.newfishchatserver.core.rpc.registry.AbstractRegister;

public abstract class AbstractConsumerBootstrap<T> implements Destroyable {

    protected T proxyInstance;
    protected ConsumerConfig consumerConfig;
    protected ProxyFactory proxyFactory;

    protected AbstractRegister register;

    public AbstractConsumerBootstrap(ConsumerConfig<T> consumerConfig, AbstractRegister register) {
        this.consumerConfig = consumerConfig;
        proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).
                getExtension(ProxyFactory.class, this.consumerConfig.getProxy());
        this.register = register;
    }

}
