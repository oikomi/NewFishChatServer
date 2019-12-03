package org.miaohong.newfishchatserver.core.rpc.network;

public interface NetworkConfig {

    String getHost();

    int getPort();

    int getThreadsNum();
}
