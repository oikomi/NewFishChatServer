package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;
import org.miaohong.newfishchatserver.core.rpc.register.Register;
import org.miaohong.newfishchatserver.core.rpc.register.RegisterRole;

public abstract class AbstractConsumerBootstrap<T> implements Destroyable, RegisterRole {

    protected T proxyInstance;
    protected ConsumerConfig<T> consumerConfig;
    protected ProxyFactory proxyFactory;

    protected Register register;

    public AbstractConsumerBootstrap(ConsumerConfig<T> consumerConfig) {
        Preconditions.checkNotNull(consumerConfig);
        this.consumerConfig = consumerConfig;
        this.proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).
                getExtension(ProxyFactory.class, this.consumerConfig.getProxy());
        this.register = ExtensionLoader.getExtensionLoader(Register.class).
                getExtension(Register.class, this.consumerConfig.getRegister());
    }

    protected void startRegister() {
        register.start(this);
        register.subscribe(consumerConfig);
    }

}
