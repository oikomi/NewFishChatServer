package org.miaohong.newfishchatserver.core.rpc.network.client.transport;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.proto.framecoder.FrameCoderProto;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO))
                .addLast(RpcEncoder.NAME, new RpcEncoder(RpcRequest.class))
                .addLast(new LengthFieldBasedFrameDecoder(FrameCoderProto.MAX_FRAME_LENGTH, 0,
                        FrameCoderProto.LENGTH_FIELD_LENGTH, 0, 0))
                .addLast(RpcDecoder.NAME, new RpcDecoder(RpcResponse.class))
                .addLast(NettyClientHandler.NAME, new NettyClientHandler());
    }
}
