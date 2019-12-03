package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Getter;
import org.miaohong.newfishchatserver.core.rpc.network.config.NetworkConfig;

public class ServerStartedEvent implements Event {

    @Getter
    private NetworkConfig serverConfig;

    public ServerStartedEvent(NetworkConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
