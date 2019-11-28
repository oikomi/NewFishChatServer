package org.miaohong.newfishchatserver.core.rpc.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import org.miaohong.newfishchatserver.core.execption.CoreErrorConstant;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.metrics.MetricSystem;
import org.miaohong.newfishchatserver.core.rpc.server.config.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.server.transport.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RPCServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(RPCServer.class);

    private NettyServer nettyServer;

    private MetricSystem metricSystem = MetricSystem.get();

    @Getter
    private volatile ServerState serverState = ServerState.INIT;

    public RPCServer(ServerConfig serverConfig) {
        try {
            nettyServer = new NettyServer(serverConfig);
            serverState = ServerState.ALIVE;
        } catch (Exception e) {
            LOG.error("RPCServer init failed {}", e.getMessage(), e);
            throw new ServerCoreException(e, CoreErrorConstant.SERVER_DEFAULT_ERROR);
        } finally {
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
        metricSystem.start();
        nettyServer.start();
    }

    @Override
    public void shutDown() {
        nettyServer.shutdown();
    }

    @Override
    public void destroy() {
        LOG.info("[LifeCycle] {} destroy", RPCServer.class.getName());
        shutDown();
    }

    @Override
    public void destroy(DestroyHook hook) {
        if (hook != null) {
            hook.preDestroy();
        }
        destroy();
        if (hook != null) {
            hook.postDestroy();
        }
    }
}
