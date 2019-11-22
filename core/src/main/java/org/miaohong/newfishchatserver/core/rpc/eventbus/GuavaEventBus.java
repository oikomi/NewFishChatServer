package org.miaohong.newfishchatserver.core.rpc.eventbus;

import org.miaohong.newfishchatserver.core.rpc.eventbus.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuavaEventBus implements EventBus {

    private static final Logger LOG = LoggerFactory.getLogger(GuavaEventBus.class);

    private final com.google.common.eventbus.EventBus eventBus;

    public GuavaEventBus(String identifier) {
        this.eventBus = new com.google.common.eventbus.EventBus(identifier);
    }

    @Override
    public void register(Object subscriber) {
        LOG.info("[eventbus] register {}", subscriber);
        this.eventBus.register(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        LOG.info("[eventbus] unregister {}", subscriber);
        this.eventBus.unregister(subscriber);
    }

    @Override
    public void post(Event event) {
        LOG.info("[eventbus] post {}", event);
        this.eventBus.post(event);
    }

}
