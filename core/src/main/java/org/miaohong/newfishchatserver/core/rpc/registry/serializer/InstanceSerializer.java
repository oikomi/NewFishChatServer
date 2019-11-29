package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

public interface InstanceSerializer<T> {
    byte[] serialize(ServiceInstance<T> instance) throws Exception;

    ServiceInstance<T> deserialize(byte[] bytes) throws Exception;
}
