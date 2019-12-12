package org.miaohong.newfishchatserver.core.rpc.register.serializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonInstanceSerializer implements InstanceSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(JsonInstanceSerializer.class);

    private final ObjectMapper mapper;
    private final JavaType type;

    private JsonInstanceSerializer() {
        this(false);
    }

    private JsonInstanceSerializer(boolean failOnUnknownProperties) {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        type = mapper.getTypeFactory().constructType(new TypeReference<ServiceInstance>() {
        });
    }

    public static JsonInstanceSerializer get() {
        return JsonInstanceSerializer.SingletonHolder.INSTANCE;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public ServiceInstance deserialize(byte[] bytes) throws Exception {
        return mapper.readValue(bytes, type);
    }

    @Override
    public byte[] serialize(ServiceInstance instance) throws Exception {
        return mapper.writeValueAsBytes(instance);
    }


    private static class SingletonHolder {
        private static final JsonInstanceSerializer INSTANCE = new JsonInstanceSerializer();
    }

}
