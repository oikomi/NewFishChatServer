package org.miaohong.newfishchatserver.core.conf;

public class RpcClientConfig extends BaseConfig {

    private static final String RPC_CLIENT_PROP_NAME = "config/rpcclient.properties";

    private RpcClientConfig() {
    }

    public static RpcClientConfig getINSTANCE() {
        return RpcClientConfig.Inner.INSTANCE;
    }

    @Override
    protected String getPropertiesPath() {
        return RPC_CLIENT_PROP_NAME;
    }

    public String getRpcClientProxy() {
        return getString("rpc.client.proxy");
    }

    private static class Inner {
        private static final RpcClientConfig INSTANCE = new RpcClientConfig();
    }

}
