package org.miaohong.newfishchatserver.core.rpc.registry;

import org.apache.zookeeper.CreateMode;
import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;

public abstract class AbstractRegister implements Register, Destroyable {

    protected static final byte[] SERVICE_OFFLINE = new byte[]{0};
    protected static final byte[] SERVICE_ONLINE = new byte[]{1};

    protected RegistryPropConfig registryPropConfig;
    protected RegisterRole registerRole;

    public AbstractRegister(RegistryPropConfig registryPropConfig) {
        this.registryPropConfig = registryPropConfig;
    }

//    public abstract void start(RegisterRole registerRole);
//
//    public abstract void register(final ServiceConfig config);
//
//    public abstract void unRegister(final ServiceConfig config);
//
//    public abstract List<String> subscribe(final ConsumerConfig config);

    protected CreateMode getCreateMode(final ServiceConfig serviceConfig) {
        CreateMode mode;
        switch (serviceConfig.getServiceType()) {
            case DYNAMIC:
                mode = CreateMode.EPHEMERAL;
                break;
            case DYNAMIC_SEQUENTIAL:
                mode = CreateMode.EPHEMERAL_SEQUENTIAL;
                break;
            default:
                mode = CreateMode.PERSISTENT;
                break;
        }

        return mode;
    }


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
