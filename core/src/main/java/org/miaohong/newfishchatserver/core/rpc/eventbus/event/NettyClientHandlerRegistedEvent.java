package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Getter;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;

public class NettyClientHandlerRegistedEvent implements Event {

    @Getter
    private String serverAddr;

    @Getter
    private NettyClientHandler nettyClientHandler;

    public NettyClientHandlerRegistedEvent(String serverAddr, NettyClientHandler nettyClientHandler) {
        this.serverAddr = serverAddr;
        this.nettyClientHandler = nettyClientHandler;
    }
}
