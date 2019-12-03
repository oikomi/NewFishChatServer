package org.miaohong.newfishchatserver.core.rpc.network.server.transport;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.miaohong.newfishchatserver.annotations.Internal;
import org.miaohong.newfishchatserver.core.conf.prop.CommonNettyPropConfig;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBus;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBusManager;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.ServerStartedEvent;
import org.miaohong.newfishchatserver.core.rpc.network.AbstractNettyComponet;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkRole;
import org.miaohong.newfishchatserver.core.rpc.network.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * core server implement with netty
 */
@Internal
public class NettyServer extends AbstractNettyComponet {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private final CommonNettyPropConfig commonNettyPropConfig = CommonNettyPropConfig.get();
    private final EventBus eventBus = EventBusManager.get();
    private String serverName;
    private ChannelFuture bindFuture;

    public NettyServer(ServerConfig serverConfig) {
        super(serverConfig);
        this.serverName = serverConfig.getServerName();
    }

    private void initCheck() {
        Preconditions.checkState(serverBootstrap == null, "Netty server has already been initialized.");
        Preconditions.checkNotNull(commonNettyPropConfig);
    }

    private void init() {
        final long start = System.currentTimeMillis();

        initCheck();
        initBootstrap(NetworkRole.SERVER);

        bindFuture = serverBootstrap.bind().syncUninterruptibly();
        ChannelFuture channelFuture = bindFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                final long duration = System.currentTimeMillis() - start;
                LOG.info("Netty Server[{}] bind to {}:{} success (took {} ms)!",
                        serverName, config.getHost(), config.getPort(), duration);

                eventBus.post(new ServerStartedEvent(config));
            } else {
                LOG.error("Netty Server[{}] bind to {}:{} failed!",
                        serverName, config.getHost(), config.getPort());
                shutdown();
            }
        });

        channelFuture.channel().closeFuture().syncUninterruptibly();
    }

    public void start() {
        init();
    }

    public void shutdown() {
        final long start = System.currentTimeMillis();
        if (bindFuture != null) {
            bindFuture.channel().close().awaitUninterruptibly();
            bindFuture = null;
        }

        if (serverBootstrap != null) {
            if (serverBootstrap.config().group() != null) {
                serverBootstrap.config().group().shutdownGracefully();
            }
            serverBootstrap = null;
        }
        final long duration = (System.currentTimeMillis() - start);
        LOG.info("Successful shutdown (took {} ms).", duration);
    }

}
