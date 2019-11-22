package org.miaohong.newfishchatserver.core.rpc.client.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.miaohong.newfishchatserver.core.execption.ClientCoreException;
import org.miaohong.newfishchatserver.core.rpc.channel.ChannelState;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.NettyClientHandlerRegistedEvent;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.proto.framecoder.FrameCoderProto;
import org.miaohong.newfishchatserver.core.util.HardwareUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class NettyClient {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);
    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(
            Math.max(HardwareUtils.getNumberCPUCores() + 1, 32),
            new DefaultThreadFactory("NettyClientWorker", true));

    private io.netty.channel.Channel channel = null;
    private InetSocketAddress localAddress = null;

    private ReentrantLock lock = new ReentrantLock();

    private Condition connected = lock.newCondition();

    private volatile ChannelState state = ChannelState.UNINIT;

    private Bootstrap bootstrap;

    private ConsumerConfig consumerConfig;

    public NettyClient(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }


    public boolean isAvailable() {
        return state.isAliveState() && channel != null && channel.isActive();
    }


    public void open() {
        if (isAvailable()) {
            return;
        }
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO))
                        .addLast(new RpcEncoder(RpcRequest.class))
                        .addLast(new LengthFieldBasedFrameDecoder(FrameCoderProto.MAX_FRAME_LENGTH, 0,
                                FrameCoderProto.LENGTH_FIELD_LENGTH, 0, 0))
                        .addLast(new RpcDecoder(RpcResponse.class))
                        .addLast(new NettyClientHandler());
            }
        });
    }

    public void connect(InetSocketAddress remoteAddress) {
        long start = System.currentTimeMillis();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = this.bootstrap.connect(remoteAddress);

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

    public synchronized void close(int timeout) {
        try {
            state = ChannelState.CLOSE;

            if (channel != null) {
                channel.close();
            }
        } catch (Exception e) {
            LOG.error("NettyChannel close Error: " + " local=" + localAddress, e);
        }
    }
}
