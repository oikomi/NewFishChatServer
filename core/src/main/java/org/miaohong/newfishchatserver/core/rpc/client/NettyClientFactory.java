package org.miaohong.newfishchatserver.core.rpc.client;

import org.miaohong.newfishchatserver.core.rpc.network.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClient;

public class NettyClientFactory {

    public static synchronized NettyClient getClient(NetworkConfig clientConfig) {
        try {
            return new NettyClient(clientConfig);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
