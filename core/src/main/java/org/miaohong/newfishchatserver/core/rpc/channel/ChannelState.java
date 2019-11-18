package org.miaohong.newfishchatserver.core.rpc.channel;

public enum ChannelState {
    UNINIT(0),
    INIT(1),
    ALIVE(2),
    UNALIVE(3),
    CLOSE(4);

    public final int value;

    private ChannelState(int value) {
        this.value = value;
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

    public boolean isInitState() {
        return this == INIT;
    }

    public boolean isUnInitState() {
        return this == UNINIT;
    }
}
