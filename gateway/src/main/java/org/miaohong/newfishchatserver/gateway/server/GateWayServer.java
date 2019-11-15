package org.miaohong.newfishchatserver.gateway.server;

import org.miaohong.newfishchatserver.core.conf.Config;
import org.miaohong.newfishchatserver.core.rpc.server.RPCServer;
import org.miaohong.newfishchatserver.core.util.DateUtils;
import org.miaohong.newfishchatserver.gateway.config.GatewayServerConfig;
import org.miaohong.newfishchatserver.proto.gateway.GatewayImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class GateWayServer {

    private static final Logger LOG = LoggerFactory.getLogger(GateWayServer.class);

    private static String buildServerName(String addr, int port) {
        return String.format("gateway-%s:%d-%s", addr, port, DateUtils.dateToStr(new Date()));
    }

    public static void main(String[] args) {
        Config config = new GatewayServerConfig();
        RPCServer rpcServer = new RPCServer(buildServerName(config.getString("server.bind.addr"),
                config.getInt("server.bind.port", 15000)), config.getString("server.bind.addr"),
                config.getInt("server.bind.port", 15000));
        rpcServer.addService("org.miaohong.newfishchatserver.proto.gateway.GatewayProto", new GatewayImpl());
        for (Class c : GatewayImpl.class.getInterfaces()) {
        }
        rpcServer.start();
    }
}
