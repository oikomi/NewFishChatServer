package org.miaohong.newfishchatserver.core.rpc.server.config;

import org.miaohong.newfishchatserver.core.util.HardwareUtils;

public abstract class AbstractServerConfig {

    protected final int serverNumThreads = Math.min(HardwareUtils.getNumberCPUCores() + 1, 32);
    protected String serverName;
    protected String host;
    protected int port;

}
