package org.miaohong.newfishchatserver.core.rpc.server.transport;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.miaohong.newfishchatserver.core.conf.prop.CommonNettyPropConfig;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.proto.framecoder.FrameCoderProto;
import org.miaohong.newfishchatserver.core.rpc.server.transport.handler.NettyServerChannelManagerHandler;
import org.miaohong.newfishchatserver.core.rpc.server.transport.handler.NettyServerMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ServerchannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerchannelInitializer.class);
    private NettyServerChannelManagerHandler channelManagerHandler;
    private NettyServerMessageHandler messageHandler;

    public ServerchannelInitializer(CommonNettyPropConfig nettyPropConfig) {
        this.channelManagerHandler = new NettyServerChannelManagerHandler();
        this.messageHandler = new NettyServerMessageHandler(nettyPropConfig);
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        LOG.info("enter initChannel");
        ch.pipeline()
                .addLast(NettyServerChannelManagerHandler.NAME, channelManagerHandler)
                .addLast(new LengthFieldBasedFrameDecoder(FrameCoderProto.MAX_FRAME_LENGTH,
                        0, FrameCoderProto.LENGTH_FIELD_LENGTH, 0, 0))
                .addLast(RpcDecoder.NAME, new RpcDecoder(RpcRequest.class))
                .addLast(RpcEncoder.NAME, new RpcEncoder(RpcResponse.class))
                // FIXME
                .addLast("server-idle-handler",
                        new IdleStateHandler(0, 0, 1000, MILLISECONDS))
                .addLast(NettyServerMessageHandler.NAME, messageHandler);
    }
}
