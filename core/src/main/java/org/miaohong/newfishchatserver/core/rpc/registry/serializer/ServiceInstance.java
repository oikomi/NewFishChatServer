package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

import lombok.Data;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;

@Data
public class ServiceInstance<T> {

    private String interfaceId;
    private String host;
    private int port;
    private long registrationTimeUTC;
    private T payload;
    private boolean isAlive;

    public ServiceInstance(String interfaceId, String host, int port, T payload, long registrationTimeUTC, boolean isAlive) {
        this.interfaceId = interfaceId;
        this.host = host;
        this.port = port;
        this.payload = payload;
        this.registrationTimeUTC = registrationTimeUTC;
        this.isAlive = isAlive;

    }

    public static <T> ServiceInstanceBuilder<T> builder(ServiceConfig serviceConfig) throws Exception {

        return new ServiceInstanceBuilder<T>()
                .host(serviceConfig.getServerConfig().getHost())
                .port(serviceConfig.getServerConfig().getPort())
                .interfaceId(serviceConfig.getInterfaceId())
                .registrationTimeUTC(System.currentTimeMillis());
    }

}
