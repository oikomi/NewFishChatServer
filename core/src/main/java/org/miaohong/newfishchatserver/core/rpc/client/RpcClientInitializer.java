package org.miaohong.newfishchatserver.core.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.proto.framecoder.FrameCoderProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(RpcClientInitializer.class);

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        LOG.info("initChannel");
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder(RpcRequest.class));
        cp.addLast(new LengthFieldBasedFrameDecoder(FrameCoderProto.MAX_FRAME_LENGTH, 0,
                FrameCoderProto.LENGTH_FIELD_LENGTH, 0, 0));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        cp.addLast(new NettyClientHandler());

        LOG.info("initChannel done");

    }
}
