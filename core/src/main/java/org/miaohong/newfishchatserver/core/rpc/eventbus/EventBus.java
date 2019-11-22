package org.miaohong.newfishchatserver.core.rpc.eventbus;

import org.miaohong.newfishchatserver.core.rpc.eventbus.event.Event;

public interface EventBus {

    void register(Object subscriber);

    void unregister(Object subscriber);

    void post(Event event);

}
