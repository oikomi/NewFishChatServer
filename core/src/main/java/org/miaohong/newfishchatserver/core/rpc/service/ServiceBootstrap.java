package org.miaohong.newfishchatserver.core.rpc.service;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.rpc.concurrency.NamedThreadFactory;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceBootstrap<T> {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new NamedThreadFactory("service register"));
    private Register register;
    private ServiceConfig<T> serviceConfig;

    public ServiceBootstrap(Register register, ServiceConfig<T> serviceConfig) {
        this.register = register;
        this.serviceConfig = serviceConfig;
    }

    public void export() {
        Preconditions.checkState(serviceConfig.getServerConfig() != null,
                "service config can not empty");
        executorService.submit(() -> {
            register.start();
            register.register(serviceConfig);
        });
    }

    public void unExport() {
    }

}
