package org.miaohong.newfishchatserver.core.rpc.registry;

public abstract class Register {
    protected RegistryConfig registryConfig;

    public Register(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    protected abstract boolean start();

}
