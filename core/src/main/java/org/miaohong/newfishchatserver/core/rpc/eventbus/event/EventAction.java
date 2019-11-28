package org.miaohong.newfishchatserver.core.rpc.eventbus.event;

public enum EventAction {

    ADD(0),
    DEL(1);

    public final int value;

    EventAction(int value) {
        this.value = value;
    }

    public boolean isAddState() {
        return this == ADD;
    }

    public boolean isDelState() {
        return this == DEL;
    }
}
