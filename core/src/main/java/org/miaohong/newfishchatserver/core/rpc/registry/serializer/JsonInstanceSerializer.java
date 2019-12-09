package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonInstanceSerializer<T> implements InstanceSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(JsonInstanceSerializer.class);

    private final ObjectMapper mapper;
    private final Class<T> payloadClass;
    private final JavaType type;

    public JsonInstanceSerializer(Class<T> payloadClass) {
        this(payloadClass, false);
    }

    @VisibleForTesting
    public JsonInstanceSerializer(Class<T> payloadClass, boolean failOnUnknownProperties) {
        this.payloadClass = payloadClass;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        type = mapper.getTypeFactory().constructType(new TypeReference<ServiceInstance>() {
        });
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public ServiceInstance deserialize(byte[] bytes) throws Exception {
        ServiceInstance rawServiceInstance = mapper.readValue(bytes, type);
        payloadClass.cast(rawServiceInstance.getServerConfig());
        return rawServiceInstance;
    }

    @Override
    public byte[] serialize(ServiceInstance instance) throws Exception {
        return mapper.writeValueAsBytes(instance);
    }

}
