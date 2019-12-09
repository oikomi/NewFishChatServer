package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

import lombok.Getter;
import lombok.Setter;
import org.miaohong.newfishchatserver.core.rpc.network.server.config.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;

public class ServiceInstance {

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
    private ServerConfig serverConfig;

    @Getter
    @Setter
    private boolean isAlive;

    private ServiceInstance() {

    }

    public ServiceInstance(String interfaceId, String host, int port, ServerConfig serverConfig, long registrationTimeUtc, boolean isAlive) {
        this.interfaceId = interfaceId;
        this.host = host;
        this.port = port;
        this.serverConfig = serverConfig;
        this.registrationTimeUtc = registrationTimeUtc;
        this.isAlive = isAlive;

    }

    public static ServiceInstanceBuilder builder(ServiceConfig serviceConfig) {

        return new ServiceInstanceBuilder()
                .host(serviceConfig.getServerConfig().getHost())
                .port(serviceConfig.getServerConfig().getPort())
                .interfaceId(serviceConfig.getInterfaceId())
                .serverConfig((ServerConfig) serviceConfig.getServerConfig())
                .registrationTimeUTC(System.currentTimeMillis());
    }

    public String getServerAddr() {
        return host + ":" + port;
    }

}
