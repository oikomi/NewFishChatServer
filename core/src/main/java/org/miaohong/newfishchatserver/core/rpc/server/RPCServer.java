package org.miaohong.newfishchatserver.core.rpc.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
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

    private ServerConfig serverConfig;

    @Getter
    private volatile ServerState serverState = ServerState.INIT;

    public RPCServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        try {
            nettyServer = new NettyServer(this.serverConfig.getServerName(),
                    this.serverConfig.getHost(), this.serverConfig.getPort(),
                    new ServerMetricGroup(new MetricRegistryImpl()));
            serverState = ServerState.ALIVE;
        } catch (Exception e) {
            LOG.error("RPCServer init failed {}", e.getMessage(), e);
            throw new ServerCoreException(e, CoreErrorConstant.SERVER_DEFAULT_ERROR);
        } finally {
            shutDown();
            serverState = ServerState.CLOSE;
        }
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void start() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serverName), "server name is null");
        Preconditions.checkNotNull(nettyServer);
        nettyServer.start();
    }

    @Override
    public void shutDown() {
        nettyServer.shutdown();
    }

}
