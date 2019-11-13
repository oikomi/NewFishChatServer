package org.miaohong.newfishchatserver.core.conf;


public class NettyConfig extends BaseConfig {

    private static final String NETTY_PROP_NAME = "netty.properties";

    public NettyConfig() {
    }

    @Override
    protected String getPropertiesPath() {
        return NETTY_PROP_NAME;
    }
}
