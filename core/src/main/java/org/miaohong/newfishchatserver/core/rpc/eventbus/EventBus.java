package org.miaohong.newfishchatserver.core.rpc.eventbus;

public interface EventBus {

    void register(Object subscriber);

    void unregister(Object subscriber);

    void post(Event event);

}
