package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Data;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;

@Data
public class NettyClientHandlerRegistedEvent implements Event {

    private String serverAddr;

    private NettyClientHandler nettyClientHandler;

    public NettyClientHandlerRegistedEvent(String serverAddr, NettyClientHandler nettyClientHandler) {
        this.serverAddr = serverAddr;
        this.nettyClientHandler = nettyClientHandler;
    }
}
