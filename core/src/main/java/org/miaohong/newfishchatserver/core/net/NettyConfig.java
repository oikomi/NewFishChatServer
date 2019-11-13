package org.miaohong.newfishchatserver.core.net;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
public class NettyConfig {

    public static final String SERVER_THREAD_GROUP_NAME = "Netty Server";
    public static final String CLIENT_THREAD_GROUP_NAME = "Netty Client";
    private static final Logger LOG = LoggerFactory.getLogger(NettyConfig.class);
    private final InetAddress serverAddress;

    private final int serverNumThreads;

    private final int serverPort;

    public NettyConfig(
            String serverAddr,
            int serverPort,
            int serverNumThreads) throws UnknownHostException {
        serverAddress = InetAddress.getByName(serverAddr);
        Preconditions.checkArgument(serverPort >= 0 && serverPort <= 65535, "Invalid port number.");
        this.serverPort = serverPort;
        this.serverNumThreads = serverNumThreads;
    }

}
