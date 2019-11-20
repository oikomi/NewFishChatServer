package org.miaohong.newfishchatserver.core.rpc.eventbus;

import lombok.Data;

@Data
public class ServiceRegistedEvent implements Event {

    private Object ref;
    private String interfaceId;

    public ServiceRegistedEvent(String interfaceId, Object ref) {
        this.interfaceId = interfaceId;
        this.ref = ref;
    }
}
