package org.miaohong.newfishchatserver.core.rpc.register.serializer;

import org.miaohong.newfishchatserver.core.rpc.network.server.config.ServerConfig;

public class ServiceInstanceBuilder {

    private String interfaceId;
    private String host;
    private int port;
    private long registrationTimeUTC;
    private ServerConfig serverConfig;
    private boolean isAlive;

    public ServiceInstanceBuilder host(String host) {
        this.host = host;
        return this;
    }

    public ServiceInstanceBuilder interfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }

    public ServiceInstanceBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServiceInstanceBuilder registrationTimeUTC(long registrationTimeUTC) {
        this.registrationTimeUTC = registrationTimeUTC;
        return this;
    }

    public ServiceInstanceBuilder serverConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public ServiceInstance build() {
        return new ServiceInstance(interfaceId, host, port, serverConfig, registrationTimeUTC, isAlive);
    }

}
