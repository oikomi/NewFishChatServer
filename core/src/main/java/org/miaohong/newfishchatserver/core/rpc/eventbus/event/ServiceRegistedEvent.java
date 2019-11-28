package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

import lombok.Data;

@Data
public class ServiceRegistedEvent implements Event {

    private EventAction action;
    private String interfaceId;
    private Object ref;

    public ServiceRegistedEvent(EventAction action, String interfaceId, Object ref) {
        this.action = action;
        this.interfaceId = interfaceId;
        this.ref = ref;
    }
}
