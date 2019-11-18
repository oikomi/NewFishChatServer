package org.miaohong.newfishchatserver.core.rpc.channel;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public abstract class AbstractChannel {
    protected Channel channel;

    public abstract void writeAndFlush(Object obj);

}
