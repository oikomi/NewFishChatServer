package org.miaohong.newfishchatserver.core.rpc.registry;

import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.ServiceConfig;

import java.util.List;

public abstract class Register implements Destroyable {

    protected RegistryPropConfig registryPropConfig;

    public Register(RegistryPropConfig registryPropConfig) {
        this.registryPropConfig = registryPropConfig;
    }

    public abstract boolean start();

    public abstract void register(ServiceConfig config);

    public abstract List<String> subscribe(final ConsumerConfig config);


    @Override
    public void destroy(DestroyHook hook) {
        if (hook != null) {
            hook.preDestroy();
        }
        destroy();
        if (hook != null) {
            hook.postDestroy();
        }
    }

}
