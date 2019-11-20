package org.miaohong.newfishchatserver.core.rpc.eventbus;

public class EventBusManager {

    private static class SingletonHolder {
        private static EventBus INSTANCE = new GuavaEventBus("tc");
    }

    public static EventBus get() {
        return SingletonHolder.INSTANCE;
    }

}
