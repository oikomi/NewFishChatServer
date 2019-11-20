package org.miaohong.newfishchatserver.gateway.server;

import org.miaohong.newfishchatserver.core.conf.PropConfig;
import org.miaohong.newfishchatserver.core.rpc.server.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.ServiceConfig;
import org.miaohong.newfishchatserver.core.runtime.JvmShutdownSafeguard;
import org.miaohong.newfishchatserver.core.runtime.SignalHandler;
import org.miaohong.newfishchatserver.core.util.DateUtils;
import org.miaohong.newfishchatserver.gateway.config.GatewayServerConfig;
import org.miaohong.newfishchatserver.proto.gateway.GatewayImpl;
import org.miaohong.newfishchatserver.proto.gateway.GatewayProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class GateWayServer {

    private static final Logger LOG = LoggerFactory.getLogger(GateWayServer.class);

    private static String buildServerName(String addr, int port) {
        return String.format("gateway-%s:%d-%s", addr, port, DateUtils.dateToStr(new Date()));
    }

    public static void main(String[] args) {

        SignalHandler.register(LOG);
        JvmShutdownSafeguard.installAsShutdownHook(LOG);

        PropConfig propConfig = new GatewayServerConfig();

        ServerConfig serverConfig = new ServerConfig()
                .setServerName(buildServerName(propConfig.getString("server.bind.addr"),
                        propConfig.getInt("server.bind.port", 15000)))
                .setHost(propConfig.getString("server.bind.addr"))
                .setPort(propConfig.getInt("server.bind.port", 15000))
                .buildIfAbsent();

        ServiceConfig<GatewayProto> serviceConfig = new ServiceConfig<>()
                .setInterfaceId(GatewayProto.class.getName())
                .setRef(new GatewayImpl())
                .setServerConfig(serverConfig);

        serviceConfig.export();
    }
}
