package org.miaohong.newfishchatserver.core.rpc.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientChannel extends AbstractChannel {

    private static final Logger LOG = LoggerFactory.getLogger(ClientChannel.class);

    @Override
    public void writeAndFlush(final Object obj) {
        Future future = channel.writeAndFlush(obj);
        future.addListener(new FutureListener() {
            @Override
            public void operationComplete(Future future1) {
                if (!future1.isSuccess()) {
                    Throwable throwable = future1.cause();
                    LOG.error("Failed to send to "
                            + channel.remoteAddress()
                            + " for msg : " + obj
                            + ", Cause by:", throwable);
                }
            }
        });
    }

}
