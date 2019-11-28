package org.miaohong.newfishchatserver.core.rpc.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.miaohong.newfishchatserver.core.execption.SystemCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyChannel implements
        org.miaohong.newfishchatserver.core.rpc.channel.Channel<ChannelHandlerContext, Channel> {

    private static final Logger LOG = LoggerFactory.getLogger(NettyChannel.class);

    private ChannelHandlerContext context;

    private Channel channel;

    public NettyChannel(ChannelHandlerContext context) {
        this.context = context;
        this.channel = context.channel();
    }

    @Override
    public ChannelHandlerContext channelContext() {
        return context;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public void writeAndFlush(final Object obj) {
        if (!isAvailable()) {
            throw new SystemCoreException("channel is not alive");
        }
        ChannelFuture channelFuture = channel.writeAndFlush(obj);
        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                Throwable throwable = future.cause();
                LOG.error("Failed to send to "
                        + channel.remoteAddress()
                        + " for msg : " + obj
                        + ", Cause by:", throwable);
            }

        });

    }

    @Override
    public boolean isAvailable() {
        return channel.isOpen() && channel.isActive();
    }

}
