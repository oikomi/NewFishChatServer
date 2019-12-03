package org.miaohong.newfishchatserver.core.rpc.network.client.transport;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.miaohong.newfishchatserver.core.execption.ClientCoreException;
import org.miaohong.newfishchatserver.core.rpc.channel.ChannelState;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.NettyClientHandlerRegistedEvent;
import org.miaohong.newfishchatserver.core.rpc.network.AbstractNettyComponet;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


public class NettyClient extends AbstractNettyComponet {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);

    private io.netty.channel.Channel channel = null;
    private InetSocketAddress localAddress = null;
    private volatile ChannelState state = ChannelState.UNINIT;

    private ConsumerConfig consumerConfig;

    public NettyClient(ConsumerConfig consumerConfig, NetworkConfig config) {
        super(config);
        this.consumerConfig = consumerConfig;
    }

    public boolean isAvailable() {
        return state.isAliveState() && channel != null && channel.isActive();
    }


    private void open() {
        if (isAvailable()) {
            return;
        }

        initBootstrap(NetworkRole.CLIENT);

        clientBootstrap.handler(new ClientChannelInitializer());
    }

    private void connect(InetSocketAddress remoteAddress) {
        long start = System.currentTimeMillis();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = clientBootstrap.connect(remoteAddress);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture channelFuture) {
                    if (channelFuture.isSuccess()) {
                        LOG.debug("Successfully connect to remote server. remote peer = {}", remoteAddress);
                        NettyClientHandler handler = channelFuture.channel().pipeline().get(NettyClientHandler.class);
                        consumerConfig.getEventBus().post(new NettyClientHandlerRegistedEvent(handler));
                    }
                }
            });

            boolean result = channelFuture.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS);
            boolean success = channelFuture.isSuccess();
            if (result && success) {
                channel = channelFuture.channel();
                if (channel.localAddress() != null && channel.localAddress() instanceof InetSocketAddress) {
                    localAddress = (InetSocketAddress) channel.localAddress();
                }
                state = ChannelState.ALIVE;
            }
            boolean connected = false;
            if (channelFuture.channel() != null) {
                connected = channelFuture.channel().isActive();
            }

        } catch (Exception e) {
            if (channelFuture != null) {
                channelFuture.channel().close();
            }
            throw new ClientCoreException("NettyChannel failed to connect to server: ", e);

        } finally {
            if (!state.isAliveState()) {
            }
        }
    }

    public void start(InetSocketAddress remoteAddress) {
        open();
        connect(remoteAddress);
    }

    public synchronized void close() {
        try {
            state = ChannelState.CLOSE;

            if (channel != null) {
                channel.close();
            }
        } catch (Exception e) {
            LOG.error("NettyChannel close Error: " + " local=" + localAddress, e);
        }
    }

    @Override
    public void destroy() {
        close();
    }

    @Override
    public void destroy(DestroyHook hook) {
        hook.preDestroy();
        destroy();
        hook.postDestroy();
    }
}
