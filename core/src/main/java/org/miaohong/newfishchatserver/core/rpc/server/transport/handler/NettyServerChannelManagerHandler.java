package org.miaohong.newfishchatserver.core.rpc.server.transport.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyServerChannelManagerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServerChannelManagerHandler.class);
    private static final int MAX_CHANNEL = 65535;
    private ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<>();
    private int maxChannel;

    public NettyServerChannelManagerHandler() {
        this(MAX_CHANNEL);
    }

    public NettyServerChannelManagerHandler(final int maxChannel) {
        super();
        LOG.info("enter NettyServerChannelManagerHandler");
        this.maxChannel = maxChannel;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("channel size is {}", channels.size());
        if (channels.size() >= maxChannel) {
            // 超过最大连接数限制，直接close连接
            LOG.warn("NettyServerChannelManage channelConnected channel size out of limit: limit={} current={}",
                    maxChannel, channels.size());
            channel.close();
        } else {
            String channelKey = getChannelKey((InetSocketAddress) channel.localAddress(),
                    (InetSocketAddress) channel.remoteAddress());
            channels.put(channelKey, channel);
            ctx.fireChannelRegistered();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String channelKey = getChannelKey((InetSocketAddress) channel.localAddress(),
                (InetSocketAddress) channel.remoteAddress());
        channels.remove(channelKey);
        ctx.fireChannelUnregistered();
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public void close() {
        for (Map.Entry<String, Channel> entry : channels.entrySet()) {
            try {
                Channel channel = entry.getValue();
                if (channel != null) {
                    channel.close();
                }
            } catch (Exception e) {
                LOG.error("NettyServerChannelManage close channel Error: " + entry.getKey(), e);
            }
        }
    }

    private String getChannelKey(InetSocketAddress local, InetSocketAddress remote) {
        String key = "";
        if (local == null || local.getAddress() == null) {
            key += "null-";
        } else {
            key += local.getAddress().getHostAddress() + ":" + local.getPort() + "-";
        }

        if (remote == null || remote.getAddress() == null) {
            key += "null";
        } else {
            key += remote.getAddress().getHostAddress() + ":" + remote.getPort();
        }

        return key;
    }

}
