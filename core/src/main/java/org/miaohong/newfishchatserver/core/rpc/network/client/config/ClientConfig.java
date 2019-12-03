package org.miaohong.newfishchatserver.core.rpc.network.client.config;

import org.miaohong.newfishchatserver.core.rpc.network.AbstractNetworkConfig;
import org.miaohong.newfishchatserver.core.util.HardwareUtils;

public class ClientConfig extends AbstractNetworkConfig {

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getThreadsNum() {
        return Math.max(HardwareUtils.getNumberCPUCores() + 1, 32);
    }
}
