package org.miaohong.newfishchatserver.core.transport;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.miaohong.newfishchatserver.core.proto.RpcDecoder;
import org.miaohong.newfishchatserver.core.proto.RpcEncoder;
import org.miaohong.newfishchatserver.core.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.server.IServiceHandler;
import org.miaohong.newfishchatserver.core.rpc.server.RpcServerHandler;

public class ServerchannelInitializer extends ChannelInitializer<SocketChannel> {

    private IServiceHandler serviceHandler;

    public ServerchannelInitializer(IServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(new RpcServerHandler(serviceHandler));

    }
}
