package org.miaohong.newfishchatserver.core.rpc.service;

public enum ServiceType {

    DYNAMIC,
    STATIC,
    PERMANENT,
    DYNAMIC_SEQUENTIAL;

    public boolean isDynamic() {
        return this == DYNAMIC || this == DYNAMIC_SEQUENTIAL;
    }

}
