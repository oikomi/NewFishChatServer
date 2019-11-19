package org.miaohong.newfishchatserver.core.rpc.server;


public class ServerFactory {

    public static synchronized Server getServer(ServerConfig serverConfig) {
        try {
            return new RPCServer(serverConfig);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
