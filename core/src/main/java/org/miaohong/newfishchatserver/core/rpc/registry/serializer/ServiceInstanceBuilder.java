package org.miaohong.newfishchatserver.core.rpc.registry.serializer;

public class ServiceInstanceBuilder<T> {

    private String interfaceId;
    private String host;
    private int port;
    private long registrationTimeUTC;
    private T payload;
    private boolean isAlive;

    public ServiceInstanceBuilder<T> host(String host) {
        this.host = host;
        return this;
    }

    public ServiceInstanceBuilder<T> interfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }

    public ServiceInstanceBuilder<T> port(int port) {
        this.port = port;
        return this;
    }

    public ServiceInstanceBuilder<T> registrationTimeUTC(long registrationTimeUTC) {
        this.registrationTimeUTC = registrationTimeUTC;
        return this;
    }

    public ServiceInstanceBuilder<T> payload(T payload) {
        this.payload = payload;
        return this;
    }

    public ServiceInstance<T> build() {
        return new ServiceInstance<>(interfaceId, host, port, payload, registrationTimeUTC, isAlive);
    }

}
