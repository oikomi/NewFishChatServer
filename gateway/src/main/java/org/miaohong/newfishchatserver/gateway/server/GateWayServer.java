package org.miaohong.newfishchatserver.gateway.server;

import org.miaohong.newfishchatserver.core.rpc.client.ConnectManager;
import org.miaohong.newfishchatserver.core.rpc.server.RPCServer;
import org.miaohong.newfishchatserver.gateway.config.GatewayServerConfig;
import org.miaohong.newfishchatserver.proto.gateway.GatewayImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateWayServer {

    private static final Logger LOG = LoggerFactory.getLogger(GateWayServer.class);

    public static void main(String[] args) {
        GatewayServerConfig config = new GatewayServerConfig();
        RPCServer rpcServer = new RPCServer(config.getString("server.bind.addr"), config.getInt("server.bind.port", 15000));
        rpcServer.addService("org.miaohong.newfishchatserver.proto.gateway.GatewayProto", new GatewayImpl());
        for (Class c : GatewayImpl.class.getInterfaces()) {
        }
        rpcServer.start();
    }
}
