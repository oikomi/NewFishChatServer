package org.miaohong.newfishchatserver.core.rpc.eventbus;

public class EventBusManager {

    public static EventBus get() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static EventBus INSTANCE = new GuavaEventBus("tc");
    }

}
