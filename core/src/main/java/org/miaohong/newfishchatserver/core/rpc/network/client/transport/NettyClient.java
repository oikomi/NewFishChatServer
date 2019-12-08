package org.miaohong.newfishchatserver.core.rpc.network.client.transport;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.miaohong.newfishchatserver.core.execption.ClientCoreException;
import org.miaohong.newfishchatserver.core.rpc.channel.ChannelState;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBus;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBusManager;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.NettyClientHandlerRegistedEvent;
import org.miaohong.newfishchatserver.core.rpc.network.AbstractNettyComponet;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


public class NettyClient extends AbstractNettyComponet implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);
    private final EventBus eventBus = EventBusManager.get();
    private io.netty.channel.Channel channel;
    private InetSocketAddress localAddress;
    private volatile ChannelState state = ChannelState.UNINIT;

    private InetSocketAddress remoteAddress;

    public NettyClient(NetworkConfig config) {
        super(config);
        this.remoteAddress = new InetSocketAddress(config.getHost(), config.getPort());
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

        Preconditions.checkState(clientBootstrap != null, "Client has not been initialized yet.");

        long start = System.currentTimeMillis();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = clientBootstrap.connect(remoteAddress);
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    NettyClientHandler handler = future.channel().pipeline().get(NettyClientHandler.class);
                    eventBus.post(new NettyClientHandlerRegistedEvent(
                            config.getHost() + ":" + config.getPort(), handler));
                    final long duration = System.currentTimeMillis() - start;
                    LOG.info("Successfully connect to remote server. remote peer = {}, (took {} ms)", remoteAddress, duration);
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

        } catch (Exception e) {
            if (channelFuture != null) {
                channelFuture.channel().close();
            }
            throw new ClientCoreException("Netty client failed to connect to server: ", e);

        } finally {
            if (!state.isAliveState()) {

            }
        }
    }

    public void start() {
        open();
        connect(remoteAddress);
    }

    public synchronized void close() {

        final long start = System.currentTimeMillis();

        try {
            state = ChannelState.CLOSE;

            if (channel != null) {
                channel.close();
            }

            if (clientBootstrap != null) {
                if (clientBootstrap.config().group() != null) {
                    clientBootstrap.config().group().shutdownGracefully();
                }
                clientBootstrap = null;
            }
            final long duration = System.currentTimeMillis() - start;
            LOG.info("Successful shutdown (took {} ms).", duration);

        } catch (Exception e) {
            LOG.error("Netty client close Error: " + " local=" + localAddress, e);
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

    @Override
    public void run() {
        start();
    }
}
