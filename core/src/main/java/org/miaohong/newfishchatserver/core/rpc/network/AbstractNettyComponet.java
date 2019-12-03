package org.miaohong.newfishchatserver.core.rpc.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.miaohong.newfishchatserver.core.conf.prop.CommonNettyPropConfig;
import org.miaohong.newfishchatserver.core.execption.FatalExitExceptionHandler;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.rpc.concurrency.NamedThreadFactory;
import org.miaohong.newfishchatserver.core.rpc.network.config.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.server.transport.ServerchannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public abstract class AbstractNettyComponet {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractNettyComponet.class);

    private final CommonNettyPropConfig commonNettyPropConfig = CommonNettyPropConfig.get();

    protected Bootstrap clientBootstrap;

    protected ServerBootstrap serverBootstrap;

    protected NetworkConfig config;

    private NettyBufferPool bufferPool;

    public AbstractNettyComponet(NetworkConfig config) {
        this.config = config;
        this.bufferPool = new NettyBufferPool(commonNettyPropConfig.getNumberOfArenas());

    }

    private static ThreadFactory getNamedThreadFactory(String name) {
        return new NamedThreadFactory(name, FatalExitExceptionHandler.INSTANCE);
    }

    protected void initBootstrap(NetworkRole role) {
        if (role.isServer()) {
            serverBootstrap = new ServerBootstrap();
        } else if (role.isClient()) {
            clientBootstrap = new Bootstrap();
        }
        switch (commonNettyPropConfig.getTransportType()) {
            case NIO:
                initNioBootstrap(role);
                break;
            case EPOLL:
                // only in linux server
                initEpollBootstrap(role);
                break;
            case AUTO:
                if (Epoll.isAvailable()) {
                    initEpollBootstrap(role);
                    LOG.info("Transport type 'auto': using EPOLL.");
                } else {
                    initNioBootstrap(role);
                    LOG.info("Transport type 'auto': using NIO.");
                }
                break;
            default:
                throw new ServerCoreException("Not support type");
        }

        setBootstrapOption(role);
    }

    private void initNioBootstrap(NetworkRole role) {
        if (role.isServer()) {
            String name = "Netty Server";
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, getNamedThreadFactory(name));
            NioEventLoopGroup workerGroup = new NioEventLoopGroup(config.getThreadsNum(), getNamedThreadFactory(name));
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        } else if (role.isClient()) {
            String name = "Netty Client";
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                    config.getThreadsNum(),
                    new DefaultThreadFactory("NettyClientWorker", true));
            clientBootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
        }
    }

    private void initEpollBootstrap(NetworkRole role) {
        if (role.isServer()) {
            String name = "Netty Server";
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, getNamedThreadFactory(name));
            EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(config.getThreadsNum(),
                    getNamedThreadFactory(name));
            serverBootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
        } else if (role.isClient()) {
            String name = "Netty Client";
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                    config.getThreadsNum(),
                    new DefaultThreadFactory("NettyClientWorker", true));
            clientBootstrap.group(eventLoopGroup).channel(EpollSocketChannel.class);
        }
    }

    private void setBootstrapOption(NetworkRole role) {
        if (role.isServer()) {
            serverBootstrap.localAddress(
                    new InetSocketAddress(config.getHost(), config.getPort()))
                    .handler(new LoggingHandler(LogLevel.INFO));
            if (commonNettyPropConfig.getChannelOptionForSOSNDBUF() > 0) {
                serverBootstrap.childOption(ChannelOption.SO_SNDBUF, commonNettyPropConfig.getChannelOptionForSOSNDBUF());
            }
            if (commonNettyPropConfig.getChannelOptionForSORCVBUF() > 0) {
                serverBootstrap.childOption(ChannelOption.SO_RCVBUF, commonNettyPropConfig.getChannelOptionForSORCVBUF());
            }
            serverBootstrap.option(ChannelOption.SO_BACKLOG, commonNettyPropConfig.getChannelOptionForSOBACKLOG())
                    .option(ChannelOption.SO_REUSEADDR, commonNettyPropConfig.getChannelOptionForSOREUSEADDR())
                    .childOption(ChannelOption.SO_KEEPALIVE, commonNettyPropConfig.getChannelOptionForSOKEEPALIVE())
                    .childOption(ChannelOption.TCP_NODELAY, commonNettyPropConfig.getgetChannelOptionForTCPNODELAY())
                    .childHandler(new ServerchannelInitializer(commonNettyPropConfig));

            // Pooled allocators for Netty's ByteBuf instances
            serverBootstrap.option(ChannelOption.ALLOCATOR, bufferPool);
            serverBootstrap.childOption(ChannelOption.ALLOCATOR, bufferPool);
        } else if (role.isClient()) {
            clientBootstrap.option(ChannelOption.SO_KEEPALIVE, commonNettyPropConfig.getChannelOptionForSOKEEPALIVE())
                    .option(ChannelOption.TCP_NODELAY, commonNettyPropConfig.getgetChannelOptionForTCPNODELAY())
                    .option(ChannelOption.ALLOCATOR, bufferPool);

            // Timeout for new connections
            clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, commonNettyPropConfig.getClientConnectTimeoutSeconds() * 1000);

        }

    }

}
