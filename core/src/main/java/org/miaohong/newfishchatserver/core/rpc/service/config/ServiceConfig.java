package org.miaohong.newfishchatserver.core.rpc.service.config;

import com.google.common.base.Objects;
import lombok.Getter;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBus;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBusManager;
import org.miaohong.newfishchatserver.core.rpc.network.config.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.server.transport.handler.NettyServerMessageHandler;
import org.miaohong.newfishchatserver.core.rpc.registry.zk.ZookeeperRegistry;
import org.miaohong.newfishchatserver.core.rpc.service.ServiceBootstrap;
import org.miaohong.newfishchatserver.core.rpc.service.ServiceType;


public class ServiceConfig<T> {

    @Getter
    private final EventBus eventBus = EventBusManager.get();
    private NetworkConfig serverConfig;
    private T ref;
    private String interfaceId;
    @Getter
    private ServiceType serviceType;
    private ServiceBootstrap serviceBootstrap;

    public ServiceConfig() {
        eventBus.register(new NettyServerMessageHandler.RpcServerHandlerListener());
        eventBus.register(new ServiceBootstrap.ServiceBootstrapListener());
        serviceType = ServiceType.DYNAMIC;
    }

    public synchronized void export() {
        if (serviceBootstrap == null) {
            serviceBootstrap = new ServiceBootstrap<>(new ZookeeperRegistry(), this);
        }
        serviceBootstrap.export();
    }

    public synchronized void unExport() {
        if (serviceBootstrap != null) {
            serviceBootstrap.unExport();
        }
    }

    public T getRef() {
        return ref;
    }

    public ServiceConfig setRef(T ref) {
        this.ref = ref;
        return this;
    }

    public NetworkConfig getServerConfig() {
        return serverConfig;
    }

    public ServiceConfig setServerConfig(NetworkConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public ServiceConfig setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceConfig<?> that = (ServiceConfig<?>) o;
        return Objects.equal(serverConfig, that.serverConfig) &&
                Objects.equal(ref, that.ref) &&
                Objects.equal(interfaceId, that.interfaceId) &&
                Objects.equal(serviceBootstrap, that.serviceBootstrap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serverConfig, ref, interfaceId, serviceBootstrap);
    }
}
