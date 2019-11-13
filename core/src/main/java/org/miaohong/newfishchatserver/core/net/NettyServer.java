package org.miaohong.newfishchatserver.core.net;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.miaohong.newfishchatserver.core.execption.FatalExitExceptionHandler;
import org.miaohong.newfishchatserver.core.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.server.IServiceHandler;
import org.miaohong.newfishchatserver.core.rpc.server.RpcServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * core server implement with netty
 */
public class NettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private static final ThreadFactoryBuilder THREAD_FACTORY_BUILDER =
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setUncaughtExceptionHandler(FatalExitExceptionHandler.INSTANCE);

    private final NettyConfig nettyConfig;

    private ServerBootstrap bootstrap;

    private ChannelFuture bindFuture;

    private InetSocketAddress localAddress;

    private IServiceHandler serviceHandler;

    public NettyServer(NettyConfig nettyConfig, IServiceHandler serviceHandler) {
        this.nettyConfig = nettyConfig;
        this.serviceHandler = serviceHandler;
    }

    public static ThreadFactory getNamedThreadFactory(String name) {
        return THREAD_FACTORY_BUILDER.setNameFormat(name + " Thread %d").build();
    }

    private void init() {
        Preconditions.checkState(bootstrap == null, "Netty server has already been initialized.");
        final long start = System.nanoTime();
        bootstrap = new ServerBootstrap();
        initNioBootstrap();
        // Server bind address
        bootstrap.localAddress(nettyConfig.getServerAddress(), nettyConfig.getServerPort());

        // --------------------------------------------------------------------
        // Start Server
        // --------------------------------------------------------------------
        bindFuture = bootstrap.bind().syncUninterruptibly();
        localAddress = (InetSocketAddress) bindFuture.channel().localAddress();
        final long duration = (System.nanoTime() - start) / 1000000L;
        LOG.info("Successful initialization (took {} ms). Listening on SocketAddress {}.", duration, localAddress);

        try {
            bindFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void initNioBootstrap() {
        // Add the server port number to the name in order to distinguish
        // multiple servers running on the same host.
        String name = NettyConfig.SERVER_THREAD_GROUP_NAME + " (" + this.nettyConfig.getServerPort() + ")";

        NioEventLoopGroup nioGroup = new NioEventLoopGroup(this.nettyConfig.getServerNumThreads(), getNamedThreadFactory(name));
        bootstrap.group(nioGroup).channel(NioServerSocketChannel.class).
                childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                .addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new RpcServerHandler(serviceHandler));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
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
