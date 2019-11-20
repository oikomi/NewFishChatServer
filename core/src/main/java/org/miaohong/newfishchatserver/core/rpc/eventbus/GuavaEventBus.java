package org.miaohong.newfishchatserver.core.rpc.eventbus;

public class GuavaEventBus implements EventBus {

    private final com.google.common.eventbus.EventBus eventBus;

    public GuavaEventBus(String identifier) {
        this.eventBus = new com.google.common.eventbus.EventBus(identifier);
    }

    @Override
    public void register(Object subscriber) {
        this.eventBus.register(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        this.eventBus.unregister(subscriber);
    }

    @Override
    public void post(Event event) {
        this.eventBus.post(event);
    }

}
