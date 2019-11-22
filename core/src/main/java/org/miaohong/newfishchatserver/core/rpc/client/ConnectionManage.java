package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.eventbus.Subscribe;
import org.miaohong.newfishchatserver.core.rpc.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.NettyClientHandlerRegistedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManage {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManage.class);

    private static CopyOnWriteArrayList<NettyClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();

    private ConnectionManage() {
    }

    public static ConnectionManage getINSTANCE() {
        return ConnectionManage.Inner.INSTANCE;
    }

    public NettyClientHandler chooseHandler() {
        return connectedHandlers.get(0);
    }

    private static class Inner {
        private static final ConnectionManage INSTANCE = new ConnectionManage();
    }

    public static class RpcClientHandlerListener {

        @Subscribe
        public void doAction(final Object event) {
            LOG.info("Received event [{}] and will take a action", event);
            if (event instanceof NettyClientHandlerRegistedEvent) {
                NettyClientHandlerRegistedEvent rpcClientRegistedEvent = (NettyClientHandlerRegistedEvent) event;
                connectedHandlers.add(rpcClientRegistedEvent.getNettyClientHandler());
            }
        }
    }
}
