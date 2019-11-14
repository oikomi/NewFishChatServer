package org.miaohong.newfishchatserver.core.rpc.server;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.net.NettyConfig;
import org.miaohong.newfishchatserver.core.net.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(RPCServer.class);

    private NettyServer nettyServer;

    private IServiceHandler serviceHandler;

    public RPCServer(String bindAddr, int bindPort) {
        serviceHandler = new RpcServiceHandler();
        try {
            nettyServer = new NettyServer(new NettyConfig(bindAddr, bindPort, 10), serviceHandler);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    void setServerName() {
        serverName = "rpc server";
    }

    @Override
    public void addService(String interfaceName, Object serviceBean) {
        serviceHandler.add(interfaceName, serviceBean);
    }

    @Override
    public void start() {
        Preconditions.checkArgument(nettyServer != null, "netty server is null");
        nettyServer.start();
    }

    @Override
    public void shutDown() {
        nettyServer.shutdown();
    }
}
