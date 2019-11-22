package org.miaohong.newfishchatserver.core.rpc.client.transport;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import org.miaohong.newfishchatserver.core.rpc.RpcHandler;
import org.miaohong.newfishchatserver.core.rpc.channel.Channel;
import org.miaohong.newfishchatserver.core.rpc.channel.NettyChannel;
import org.miaohong.newfishchatserver.core.rpc.client.RPCFuture;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements RpcHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClientHandler.class);

    private ConcurrentHashMap<String, RPCFuture> pendingRpc = new ConcurrentHashMap<>();

    @Getter
    private Channel channel;

    public NettyClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = new NettyChannel(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        String requestId = response.getRequestId();
        RPCFuture rpcFuture = pendingRpc.get(requestId);
        if (rpcFuture != null) {
            pendingRpc.remove(requestId);
            rpcFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("client caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    public RPCFuture sendRequest(final RpcRequest request) {
        RPCFuture rpcFuture = new RPCFuture(request);
        pendingRpc.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }
}
