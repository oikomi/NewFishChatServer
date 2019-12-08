package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

import lombok.Getter;
import lombok.Setter;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;

public class ServiceInstance<T> {

    @Getter
    @Setter
    private String interfaceId;

    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private long registrationTimeUtc;

    @Getter
    @Setter
    private T payload;

    @Getter
    @Setter
    private boolean isAlive;

    private ServiceInstance() {

    }

    public ServiceInstance(String interfaceId, String host, int port, T payload, long registrationTimeUtc, boolean isAlive) {
        this.interfaceId = interfaceId;
        this.host = host;
        this.port = port;
        this.payload = payload;
        this.registrationTimeUtc = registrationTimeUtc;
        this.isAlive = isAlive;

    }

    public static <T> ServiceInstanceBuilder<T> builder(ServiceConfig serviceConfig) {

        return new ServiceInstanceBuilder<T>()
                .host(serviceConfig.getServerConfig().getHost())
                .port(serviceConfig.getServerConfig().getPort())
                .interfaceId(serviceConfig.getInterfaceId())
                .payload((T) serviceConfig.getServerConfig())
                .registrationTimeUTC(System.currentTimeMillis());
    }

}
