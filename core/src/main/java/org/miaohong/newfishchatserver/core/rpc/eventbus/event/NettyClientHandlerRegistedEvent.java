package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Data;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;

@Data
public class NettyClientHandlerRegistedEvent implements Event {

    private NettyClientHandler nettyClientHandler;

    public NettyClientHandlerRegistedEvent(NettyClientHandler nettyClientHandler) {
        this.nettyClientHandler = nettyClientHandler;
    }
}
