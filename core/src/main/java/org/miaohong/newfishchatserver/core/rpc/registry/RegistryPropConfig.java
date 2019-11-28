package org.miaohong.newfishchatserver.core.rpc.registry;


import org.miaohong.newfishchatserver.core.conf.prop.BasePropConfig;

public class RegistryPropConfig extends BasePropConfig {

    private static final String REGISTRY_PROP_NAME = "config/registry.properties";

    private RegistryPropConfig() {
    }

    public static RegistryPropConfig get() {
        return Inner.INSTANCE;
    }

    public String getProtocol() {
        return getString("registry.proto");
    }

    public String getAddress() {
        return getString("registry.addr");
    }

    public int getConnectTimeout() {
        return getInt("registry.connecttimeout", 1000);
    }

    public int getTimeout() {
        return getInt("registry.timeout", 1000);
    }

    public String getRoot() {
        return getString("registry.root");
    }

    public int getRetryWait() {
        return getInt("registry.retry.wait", 3000);
    }

    public int getMaxRetryAttempts() {
        return getInt("registry.retry.max.attempts", 3);
    }

    @Override
    protected String getPropertiesPath() {
        return REGISTRY_PROP_NAME;
    }

    private static class Inner {
        private static final RegistryPropConfig INSTANCE = new RegistryPropConfig();
    }

}
