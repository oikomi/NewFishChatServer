package org.miaohong.newfishchatserver.core.rpc.network.server.config;


import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.rpc.network.AbstractNetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.server.Server;
import org.miaohong.newfishchatserver.core.rpc.server.ServerFactory;
import org.miaohong.newfishchatserver.core.util.NetUtils;

public class ServerConfig extends AbstractNetworkConfig {

    private Server server;

    @Override
    public int getThreadsNum() {
        return serverNumThreads;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerConfig setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    @Override
    public String getHost() {
        return host;
    }

    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }

    @Override
    public int getPort() {
        return port;
    }


    public ServerConfig setPort(int port) {
        if (!NetUtils.isRandomPort(port) && NetUtils.isInvalidPort(port)) {
            throw new ServerCoreException("port must between -1 and 65535 (-1 means random port)");
        }
        this.port = port;
        return this;
    }

    public Server getServer() {
        return server;
    }

    public ServerConfig setServer(Server server) {
        this.server = server;
        return this;
    }

    public ServerConfig buildIfAbsent() {
        this.server = ServerFactory.getServer(this);
        server.start();
        return this;
    }
}
