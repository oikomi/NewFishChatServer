package org.miaohong.newfishchatserver.core.rpc.server;


import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.rpc.concurrency.NamedThreadFactory;
import org.miaohong.newfishchatserver.core.util.NetUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConfig extends AbstractServerConfig {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new NamedThreadFactory("server start"));
    private String serverName;
    private String host;
    private int port;
    private Server server;

    public String getServerName() {
        return serverName;
    }

    public ServerConfig setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }

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
        executorService.submit(() -> server.start());
        return this;
    }
}
