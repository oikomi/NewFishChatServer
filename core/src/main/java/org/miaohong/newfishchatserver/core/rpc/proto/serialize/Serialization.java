package org.miaohong.newfishchatserver.core.rpc.proto.serialize;

import java.io.IOException;

public interface Serialization {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;

}
