package org.miaohong.newfishchatserver.core.rpc.server.transport;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.miaohong.newfishchatserver.core.util.HardwareUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
public class ServerNettyConfig {

    public static final String SERVER_THREAD_GROUP_NAME = "Netty Server";
    public static final String CLIENT_THREAD_GROUP_NAME = "Netty Client";
    private static final Logger LOG = LoggerFactory.getLogger(ServerNettyConfig.class);
    private static final int MAX_PORT = 65535;
    private final InetAddress serverAddress;
    private final int serverNumThreads = Math.min(HardwareUtils.getNumberCPUCores() + 1, 32);
    private final int serverPort;

    public ServerNettyConfig(
            String serverAddr,
            int serverPort) throws UnknownHostException {
        serverAddress = InetAddress.getByName(serverAddr);
        Preconditions.checkArgument(serverPort >= 0 && serverPort <= MAX_PORT, "Invalid port number.");
        this.serverPort = serverPort;
    }
}
