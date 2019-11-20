package org.miaohong.newfishchatserver.core.rpc.server;

import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.base.LifeCycle;

public abstract class Server implements LifeCycle, Destroyable {

    protected String serverName = "please set server name";

    abstract void setServerName(String serverName);
}
