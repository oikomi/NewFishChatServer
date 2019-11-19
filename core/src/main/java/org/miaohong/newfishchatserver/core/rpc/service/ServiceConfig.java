package org.miaohong.newfishchatserver.core.rpc.service;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import org.miaohong.newfishchatserver.core.rpc.registry.zk.ZookeeperRegistry;
import org.miaohong.newfishchatserver.core.rpc.server.RpcServerHandler;
import org.miaohong.newfishchatserver.core.rpc.server.ServerConfig;


public class ServiceConfig<T> {

    @Getter
    final EventBus eventBus = new EventBus();
    private ServerConfig serverConfig;
    private T ref;
    private String interfaceId;
    private ServiceBootstrap serviceBootstrap;

    public ServiceConfig() {
        eventBus.register(new RpcServerHandler.RpcServerHandlerListener());
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

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServiceConfig setServerConfig(ServerConfig serverConfig) {
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


}
