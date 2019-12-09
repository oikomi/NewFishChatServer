package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

public interface InstanceSerializer {
    byte[] serialize(ServiceInstance instance) throws Exception;

    ServiceInstance deserialize(byte[] bytes) throws Exception;
}
