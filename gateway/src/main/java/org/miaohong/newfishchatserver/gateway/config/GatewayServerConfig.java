package org.miaohong.newfishchatserver.gateway.config;

import org.miaohong.newfishchatserver.core.conf.BaseConfig;

public class GatewayServerConfig extends BaseConfig {

    private static final String SERVER_PROP_NAME = "server.properties";

    @Override
    protected String getPropertiesPath() {
        return SERVER_PROP_NAME;
    }
}
