package org.miaohong.newfishchatserver.core.rpc.server;

import org.miaohong.newfishchatserver.core.rpc.LifeCycle;

public abstract class Server implements LifeCycle {

    protected String serverName = "please set server name";

    abstract void setServerName(String serverName);

    /**
     * addService
     *
     * @param interfaceName
     * @param serviceBean
     * @return
     */
    abstract void addService(String interfaceName, Object serviceBean);


}
