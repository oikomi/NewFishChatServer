package org.miaohong.newfishchatserver.core.rpc.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.proto.serialize.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcEncoder extends MessageToByteEncoder {

    public static final String NAME = "message encoder";
    private static final Logger LOG = LoggerFactory.getLogger(RpcEncoder.class);
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        LOG.info("enter RpcEncoder");
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        LOG.info("do encode");
        if (genericClass.isInstance(in)) {
            byte[] data = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(Serialization.class,
                    "protobuf").serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
