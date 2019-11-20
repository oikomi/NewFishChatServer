package org.miaohong.newfishchatserver.core.rpc.server.transport;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.miaohong.newfishchatserver.annotations.Internal;
import org.miaohong.newfishchatserver.core.conf.CommonNettyPropConfig;
import org.miaohong.newfishchatserver.core.execption.FatalExitExceptionHandler;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.metric.MetricGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;

/**
 * core server implement with netty
 */
@Internal
public class NettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private static final ThreadFactoryBuilder THREAD_FACTORY_BUILDER =
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setUncaughtExceptionHandler(FatalExitExceptionHandler.INSTANCE);

    private final ServerNettyConfig serverNettyConfig;

    private final CommonNettyPropConfig commonNettyPropConfig;

    private String serverName;

    private ServerBootstrap bootstrap;

    private ChannelFuture bindFuture;

    private MetricGroup serverMetricGroup;

    public NettyServer(String serverName,
                       String serverAddr,
                       int serverPort,
                       MetricGroup serverMetricGroup) throws UnknownHostException {
        this.serverName = serverName;
        this.serverNettyConfig = new ServerNettyConfig(serverAddr, serverPort, 10);
        this.commonNettyPropConfig = CommonNettyPropConfig.getINSTANCE();
        this.serverMetricGroup = serverMetricGroup;
    }

    private static ThreadFactory getNamedThreadFactory(String name) {
        return THREAD_FACTORY_BUILDER.setNameFormat(name + "Thread %d").build();
    }

    private void initCheck() {
        Preconditions.checkState(bootstrap == null, "Netty server has already been initialized.");
        Preconditions.checkNotNull(serverNettyConfig);
        Preconditions.checkNotNull(commonNettyPropConfig);
        Preconditions.checkNotNull(serverMetricGroup);
    }

    private void init() {
        initCheck();

        final long start = System.currentTimeMillis();

        initBootstrap();
        setBootstrap();

        bindFuture = bootstrap.bind().syncUninterruptibly();

        ChannelFuture channelFuture = bindFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                final long duration = System.currentTimeMillis() - start;
                LOG.info("Netty Server[{}] bind to {}:{} success (took {} ms)!",
                        serverName, serverNettyConfig.getServerAddress(), serverNettyConfig.getServerPort(), duration);

            } else {
                LOG.error("Netty Server[{}] bind to {}:{} failed!",
                        serverName, serverNettyConfig.getServerAddress(), serverNettyConfig.getServerPort());
                shutdown();
            }
        });

        channelFuture.channel().closeFuture().syncUninterruptibly();
    }

    private void initBootstrap() {
        bootstrap = new ServerBootstrap();
        switch (commonNettyPropConfig.getTransportType()) {
            case NIO:
                initNioBootstrap();
                break;
            case EPOLL:
                // only in linux server
                initEpollBootstrap();
                break;
            case AUTO:
                if (Epoll.isAvailable()) {
                    initEpollBootstrap();
                    LOG.info("Transport type 'auto': using EPOLL.");
                } else {
                    initNioBootstrap();
                    LOG.info("Transport type 'auto': using NIO.");
                }
                break;
            default:
                throw new ServerCoreException("Not support type");
        }
    }

    private void initNioBootstrap() {
        String name = ServerNettyConfig.SERVER_THREAD_GROUP_NAME + " (" + serverNettyConfig.getServerPort() + ")";
        NioEventLoopGroup nioGroup = new NioEventLoopGroup(serverNettyConfig.getServerNumThreads(), getNamedThreadFactory(name));
        bootstrap.group(nioGroup).channel(NioServerSocketChannel.class);
    }

    private void initEpollBootstrap() {
        String name = ServerNettyConfig.SERVER_THREAD_GROUP_NAME + " (" + serverNettyConfig.getServerPort() + ")";
        EpollEventLoopGroup epollGroup = new EpollEventLoopGroup(serverNettyConfig.getServerNumThreads(),
                getNamedThreadFactory(name));
        bootstrap.group(epollGroup).channel(EpollServerSocketChannel.class);
    }

    private void setBootstrap() {
        bootstrap.localAddress(
                new InetSocketAddress(serverNettyConfig.getServerAddress(), serverNettyConfig.getServerPort()))
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ServerchannelInitializer(serverMetricGroup))
                .option(ChannelOption.SO_BACKLOG, commonNettyPropConfig.getChannelOptionForSOBACKLOG())
                .option(ChannelOption.SO_REUSEADDR, commonNettyPropConfig.getChannelOptionForSOREUSEADDR())
                .childOption(ChannelOption.SO_KEEPALIVE, commonNettyPropConfig.getChannelOptionForSOKEEPALIVE())
                .childOption(ChannelOption.TCP_NODELAY, commonNettyPropConfig.getgetChannelOptionForTCPNODELAY());
        if (commonNettyPropConfig.getChannelOptionForSOSNDBUF() > 0) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, commonNettyPropConfig.getChannelOptionForSOSNDBUF());
        }
        if (commonNettyPropConfig.getChannelOptionForSORCVBUF() > 0) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, commonNettyPropConfig.getChannelOptionForSORCVBUF());
        }
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

        if (bootstrap != null) {
            if (bootstrap.config().group() != null) {
                bootstrap.config().group().shutdownGracefully();
            }
            bootstrap = null;
        }
        final long duration = (System.currentTimeMillis() - start);
        LOG.info("Successful shutdown (took {} ms).", duration);
    }

}
