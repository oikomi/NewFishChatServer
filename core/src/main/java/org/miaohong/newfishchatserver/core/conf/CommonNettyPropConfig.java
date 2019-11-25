package org.miaohong.newfishchatserver.core.conf;


public class CommonNettyPropConfig extends BasePropConfig {

    private static final String NETTY_PROP_NAME = "config/netty.properties";

    private CommonNettyPropConfig() {
    }

    public static CommonNettyPropConfig get() {
        return Inner.INSTANCE;
    }

    public TransportType getTransportType() {
        String transport = getString("netty.transportType");

        switch (transport) {
            case "nio":
                return TransportType.NIO;
            case "epoll":
                return TransportType.EPOLL;
            default:
                return TransportType.AUTO;
        }
    }

    public int getChannelOptionForSOBACKLOG() {
        return getInt("netty.ChannelOption.SO_BACKLOG", 128);
    }

    public boolean getChannelOptionForSOREUSEADDR() {
        return getBoolean("netty.ChannelOption.SO_REUSEADDR", true);
    }

    public boolean getChannelOptionForSOKEEPALIVE() {
        return getBoolean("netty.ChannelOption.SO_KEEPALIVE", true);
    }

    public boolean getgetChannelOptionForTCPNODELAY() {
        return getBoolean("netty.ChannelOption.TCP_NODELAY", true);
    }

    public int getChannelOptionForSOSNDBUF() {
        return getInt("netty.ChannelOption.SO_SNDBUF", 0);
    }

    public int getChannelOptionForSORCVBUF() {
        return getInt("netty.ChannelOption.SO_RCVBUF", 0);
    }

    public int getNettyServerPoolCore() {
        return getInt("netty.server.pool.core", 0);
    }

    public int getNettyServerPoolMax() {
        return getInt("netty.server.pool.max", 0);
    }

    public int getNettyServerPoolQueue() {
        return getInt("netty.server.pool.queue", 0);
    }

    public int getNettyServerPoolAlive() {
        return getInt("netty.server.pool.alive", 0);
    }

    @Override
    protected String getPropertiesPath() {
        return NETTY_PROP_NAME;
    }

    public enum TransportType {
        NIO, EPOLL, AUTO
    }

    private static class Inner {
        private static final CommonNettyPropConfig INSTANCE = new CommonNettyPropConfig();
    }
}
