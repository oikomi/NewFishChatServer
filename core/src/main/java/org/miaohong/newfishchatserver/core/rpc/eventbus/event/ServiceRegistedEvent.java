package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Data;

@Data
public class ServiceRegistedEvent implements Event {

    private String interfaceId;
    private Object ref;

    public ServiceRegistedEvent(String interfaceId, Object ref) {
        this.interfaceId = interfaceId;
        this.ref = ref;
    }
}
