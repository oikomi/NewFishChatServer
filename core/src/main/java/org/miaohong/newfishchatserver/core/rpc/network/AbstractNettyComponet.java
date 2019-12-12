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
import org.miaohong.newfishchatserver.core.conf.prop.CommonNettyPropConfig;
import org.miaohong.newfishchatserver.core.execption.FatalExitExceptionHandler;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.concurrency.NamedThreadFactory;
import org.miaohong.newfishchatserver.core.rpc.network.server.transport.ServerchannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public abstract class AbstractNettyComponet implements Destroyable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractNettyComponet.class);

    private static final String ERR_MEG_FORMAT = "Not support type for %s";

    private final CommonNettyPropConfig commonNettyPropConfig = CommonNettyPropConfig.get();

    protected Bootstrap clientBootstrap;

    protected ServerBootstrap serverBootstrap;

    protected NetworkConfig config;

    private NettyBufferPool bufferPool;

    public AbstractNettyComponet(NetworkConfig config) {
        this.config = config;
        this.bufferPool = new NettyBufferPool(commonNettyPropConfig.getNumberOfArenas());

    }

    private ThreadFactory getNamedThreadFactory(String name) {
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
                throw new ServerCoreException(String.format(ERR_MEG_FORMAT,
                        commonNettyPropConfig.getTransportType()));
        }

        setBootstrapOption(role);
    }

    private void initNioBootstrap(NetworkRole role) {
        switch (role) {
            case SERVER:
                NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, getNamedThreadFactory("Netty Server"));
                NioEventLoopGroup workerGroup = new NioEventLoopGroup(config.getThreadsNum(), getNamedThreadFactory("Netty Server"));
                serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
                break;
            case CLIENT:
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                        config.getThreadsNum(),
                        getNamedThreadFactory("Netty Client"));
                clientBootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
                break;
            default:
                throw new ServerCoreException(String.format(ERR_MEG_FORMAT, role));

        }
    }

    private void initEpollBootstrap(NetworkRole role) {
        switch (role) {
            case SERVER:
                NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, getNamedThreadFactory("Netty Server bossGroup"));
                EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(config.getThreadsNum(),
                        getNamedThreadFactory("Netty Server workerGroup"));
                serverBootstrap.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
                break;
            case CLIENT:
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                        config.getThreadsNum(),
                        getNamedThreadFactory("Netty Client"));
                clientBootstrap.group(eventLoopGroup).channel(EpollSocketChannel.class);
                break;

            default:
                throw new ServerCoreException(String.format(ERR_MEG_FORMAT, role));

        }

    }

    private void setBootstrapOption(NetworkRole role) {
        switch (role) {
            case SERVER:
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

                break;

            case CLIENT:
                clientBootstrap.option(ChannelOption.SO_KEEPALIVE, commonNettyPropConfig.getChannelOptionForSOKEEPALIVE())
                        .option(ChannelOption.TCP_NODELAY, commonNettyPropConfig.getgetChannelOptionForTCPNODELAY())
                        .option(ChannelOption.ALLOCATOR, bufferPool);

                // Timeout for new connections
                clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, commonNettyPropConfig.getClientConnectTimeoutSeconds() * 1000);

                break;

            default:
                throw new ServerCoreException(String.format(ERR_MEG_FORMAT, role));
        }

    }

}
