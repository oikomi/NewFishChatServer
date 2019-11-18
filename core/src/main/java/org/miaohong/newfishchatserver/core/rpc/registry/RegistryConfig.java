package org.miaohong.newfishchatserver.core.rpc.registry;

import org.miaohong.newfishchatserver.core.conf.BaseConfig;

public class RegistryConfig extends BaseConfig {

    private static final String REGISTRY_PROP_NAME = "config/registry.properties";

    private RegistryConfig() {
    }

    public static RegistryConfig getINSTANCE() {
        return RegistryConfig.Inner.INSTANCE;
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

    @Override
    protected String getPropertiesPath() {
        return REGISTRY_PROP_NAME;
    }

    private static class Inner {
        private static final RegistryConfig INSTANCE = new RegistryConfig();
    }


}
