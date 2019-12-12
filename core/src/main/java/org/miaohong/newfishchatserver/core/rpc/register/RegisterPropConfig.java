package org.miaohong.newfishchatserver.core.rpc.register;


import org.miaohong.newfishchatserver.core.conf.prop.BasePropConfig;

public class RegisterPropConfig extends BasePropConfig {

    private static final String REGISTRY_PROP_NAME = "config/register.properties";

    private RegisterPropConfig() {
    }

    public static RegisterPropConfig get() {
        return SingletonHolder.INSTANCE;
    }

    public String getProtocol() {
        return getString("register.proto");
    }

    public String getAddress() {
        return getString("register.addr");
    }

    public int getConnectTimeout() {
        return getInt("register.connecttimeout", 1000);
    }

    public int getTimeout() {
        return getInt("register.timeout", 1000);
    }

    public String getRoot() {
        return getString("register.root");
    }

    public int getRetryWait() {
        return getInt("register.retry.wait", 3000);
    }

    public int getMaxRetryAttempts() {
        return getInt("register.retry.max.attempts", 3);
    }

    @Override
    protected String getPropertiesPath() {
        return REGISTRY_PROP_NAME;
    }

    private static class SingletonHolder {
        private static final RegisterPropConfig INSTANCE = new RegisterPropConfig();
    }

}
