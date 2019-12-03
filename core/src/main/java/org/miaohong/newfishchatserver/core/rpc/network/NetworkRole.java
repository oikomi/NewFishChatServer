package org.miaohong.newfishchatserver.core.rpc.network;

public enum NetworkRole {

    CLIENT(0),
    SERVER(1);

    public final int value;

    NetworkRole(int value) {
        this.value = value;
    }

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isServer() {
        return this == SERVER;
    }

}
