package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;

public class JsonInstanceSerializer<T> implements InstanceSerializer<T> {

    private final ObjectMapper mapper;
    private final Class<T> payloadClass;
    private final JavaType type;

    public JsonInstanceSerializer(Class<T> payloadClass) {
        this(payloadClass, false);
    }

    @VisibleForTesting
    JsonInstanceSerializer(Class<T> payloadClass, boolean failOnUnknownProperties) {
        this.payloadClass = payloadClass;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        type = mapper.getTypeFactory().constructType(ServiceInstance.class);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public ServiceInstance<T> deserialize(byte[] bytes) throws Exception {
        ServiceInstance rawServiceInstance = mapper.readValue(bytes, type);
        payloadClass.cast(rawServiceInstance.getPayload());
        return (ServiceInstance<T>) rawServiceInstance;
    }

    @Override
    public byte[] serialize(ServiceInstance<T> instance) throws Exception {
        return mapper.writeValueAsBytes(instance);
    }

}
