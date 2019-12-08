package org.miaohong.newfishchatserver.core.rpc.network.client.config;

import org.miaohong.newfishchatserver.core.rpc.network.AbstractNetworkConfig;
import org.miaohong.newfishchatserver.core.util.HardwareUtils;

public class ClientConfig extends AbstractNetworkConfig {

    public ClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getThreadsNum() {
        return Math.max(HardwareUtils.getNumberCPUCores() + 1, 32);
    }
}
