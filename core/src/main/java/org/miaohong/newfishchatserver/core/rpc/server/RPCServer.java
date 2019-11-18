package org.miaohong.newfishchatserver.core.rpc.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.miaohong.newfishchatserver.core.execption.CoreErrorConstant;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.metric.MetricRegistryImpl;
import org.miaohong.newfishchatserver.core.metric.metricgroup.ServerMetricGroup;
import org.miaohong.newfishchatserver.core.rpc.server.transport.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RPCServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(RPCServer.class);

    private NettyServer nettyServer;

    private IServiceHandler serviceHandler;

    public RPCServer(String serverName, String bindAddr, int bindPort) {
        serviceHandler = new RpcServiceHandler();
        try {
            nettyServer = new NettyServer(serverName, bindAddr, bindPort,
                    serviceHandler, new ServerMetricGroup(new MetricRegistryImpl()));
        } catch (Exception e) {
            LOG.error("RPCServer init failed {}", e.getMessage(), e);
            throw new ServerCoreException(e, CoreErrorConstant.SERVER_DEFAULT_ERROR);
        }
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void addService(String interfaceName, Object serviceBean) {
        serviceHandler.add(interfaceName, serviceBean);
    }

    @Override
    public void start() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serverName), "server name is null");
        Preconditions.checkArgument(nettyServer != null, "netty server is null");
        nettyServer.start();
    }

    @Override
    public void shutDown() {
        nettyServer.shutdown();
    }

}
