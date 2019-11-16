package org.miaohong.newfishchatserver.core.rpc.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.RpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements RpcHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RpcClientHandler.class);

    private ConcurrentHashMap<String, RPCFuture> pendingRpc = new ConcurrentHashMap<>();

    private Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        RPCFuture rpcFuture = pendingRpc.get(requestId);
        if (rpcFuture != null) {
            pendingRpc.remove(requestId);
            rpcFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("client caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RPCFuture sendRequest(RpcRequest request) {
        final CountDownLatch latch = new CountDownLatch(1);
        RPCFuture rpcFuture = new RPCFuture(request);
        pendingRpc.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> latch.countDown());
        try {
            latch.await();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return rpcFuture;
    }
}
