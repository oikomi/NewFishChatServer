package org.miaohong.newfishchatserver.core.rpc.channel;

import java.net.InetSocketAddress;

public interface Channel<CONTEXT, CHANNEL> {

    CONTEXT channelContext();

    CHANNEL channel();

    InetSocketAddress remoteAddress();

    InetSocketAddress localAddress();

    void writeAndFlush(Object obj);

    boolean isAvailable();
}
