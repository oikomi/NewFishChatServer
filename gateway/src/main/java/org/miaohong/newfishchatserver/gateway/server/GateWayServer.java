package org.miaohong.newfishchatserver.gateway.server;

import org.miaohong.newfishchatserver.core.rpc.server.RPCServer;
import org.miaohong.newfishchatserver.gateway.config.GatewayServerConfig;
import org.miaohong.newfishchatserver.proto.gateway.GatewayImpl;

public class GateWayServer {

    public static void main(String[] args) {
        GatewayServerConfig config = new GatewayServerConfig();
        RPCServer rpcServer = new RPCServer(config.getString("server.bind.addr"), config.getInt("server.bind.port", 15000));
        rpcServer.addService("org.miaohong.newfishchatserver.proto.gateway.GatewayProto", new GatewayImpl());
        System.out.println(GatewayImpl.class.getInterfaces());
        for (Class c : GatewayImpl.class.getInterfaces()) {
            System.out.println(c);
        }
        rpcServer.start();
    }
}
