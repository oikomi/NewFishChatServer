package org.miaohong.newfishchatserver.core.rpc.server;

public enum ServerState {

    INIT(0),
    ALIVE(1),
    UNALIVE(2),
    CLOSE(3);

    public final int value;

    private ServerState(int value) {
        this.value = value;
    }

    public boolean isInitState() {
        return this == INIT;
    }

    public boolean isAliveState() {
        return this == ALIVE;
    }

    public boolean isUnAliveState() {
        return this == UNALIVE;
    }

    public boolean isCloseState() {
        return this == CLOSE;
    }

}
