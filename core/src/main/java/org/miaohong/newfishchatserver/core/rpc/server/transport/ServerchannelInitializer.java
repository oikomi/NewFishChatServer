package org.miaohong.newfishchatserver.core.rpc.server.transport;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.miaohong.newfishchatserver.core.metric.MetricGroup;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.proto.framecoder.FrameCoderProto;
import org.miaohong.newfishchatserver.core.rpc.server.IServiceHandler;
import org.miaohong.newfishchatserver.core.rpc.server.RpcServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerchannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerchannelInitializer.class);

    private IServiceHandler serviceHandler;
    private MetricGroup serverMetricGroup;

    public ServerchannelInitializer(IServiceHandler serviceHandler, MetricGroup serverMetricGroup) {
        this.serviceHandler = serviceHandler;
        this.serverMetricGroup = serverMetricGroup;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {

        LOG.info("enter initChannel");

        RpcServerHandler rpcServerHandler = new RpcServerHandler(serviceHandler, serverMetricGroup);
        socketChannel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(FrameCoderProto.MAX_FRAME_LENGTH,
                        0, FrameCoderProto.LENGTH_FIELD_LENGTH, 0, 0))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(rpcServerHandler);
    }

}
