package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Getter;
import org.miaohong.newfishchatserver.core.rpc.server.ServerConfig;

public class ServerStartedEvent implements Event {

    @Getter
    private ServerConfig serverConfig;

    public ServerStartedEvent(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
