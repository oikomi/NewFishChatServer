package org.miaohong.newfishchatserver.core.rpc.client;

import org.miaohong.newfishchatserver.core.execption.ClientCoreException;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClientFactory.class);

    public static synchronized NettyClient getClient(NetworkConfig clientConfig) {
        try {
            return new NettyClient(clientConfig);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ClientCoreException(e.getMessage(), e);
        }
    }

}
