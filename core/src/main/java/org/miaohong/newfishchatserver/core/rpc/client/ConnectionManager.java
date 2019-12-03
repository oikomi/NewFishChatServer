package org.miaohong.newfishchatserver.core.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536));

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private CopyOnWriteArrayList<NettyClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();
    private Map<InetSocketAddress, NettyClientHandler> connectedServerNodes = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile boolean isRuning = true;

    private ConnectionManager() {
    }

    public static ConnectionManager getINSTANCE() {
        return SingletonHolder.INSTANCE;
    }

    public void updateConnectedServer(List<String> allServerAddress) {
        if (CommonUtils.isNotEmpty(allServerAddress)) {
            //update local serverNodes cache
            HashSet<InetSocketAddress> newAllServerNodeSet = new HashSet<>();
            for (int i = 0; i < allServerAddress.size(); ++i) {
                String[] array = allServerAddress.get(i).split(":");
                if (array.length == 2) {
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);
                    final InetSocketAddress remotePeer = new InetSocketAddress(host, port);
                    newAllServerNodeSet.add(remotePeer);
                }
            }

            // Add new server node
            for (final InetSocketAddress serverNodeAddress : newAllServerNodeSet) {
                if (!connectedServerNodes.containsKey(serverNodeAddress)) {
                    connectServerNode(serverNodeAddress);
                }
            }

            // Close and remove invalid server nodes
            for (int i = 0; i < connectedHandlers.size(); ++i) {
                NettyClientHandler connectedServerHandler = connectedHandlers.get(i);
                SocketAddress remotePeer = connectedServerHandler.getChannel().remoteAddress();
                if (!newAllServerNodeSet.contains(remotePeer)) {
                    LOG.info("Remove invalid server node {}", remotePeer);
                    NettyClientHandler handler = connectedServerNodes.get(remotePeer);
                    if (handler != null) {
                        handler.close();
                    }
                    connectedServerNodes.remove(remotePeer);
                    connectedHandlers.remove(connectedServerHandler);
                }
            }

        } else { // No available server node ( All server nodes are down )
            LOG.error("No available server node. All server nodes are down !!!");
            for (final NettyClientHandler connectedServerHandler : connectedHandlers) {
                SocketAddress remotePeer = connectedServerHandler.getChannel().remoteAddress();
                NettyClientHandler handler = connectedServerNodes.get(remotePeer);
                handler.close();
                connectedServerNodes.remove(connectedServerHandler);
            }
            connectedHandlers.clear();
        }

    }

    public void reconnect(final NettyClientHandler handler, final SocketAddress remotePeer) {
        if (handler != null) {
            connectedHandlers.remove(handler);
            connectedServerNodes.remove(handler.getChannel().remoteAddress());
        }
        connectServerNode((InetSocketAddress) remotePeer);
    }

    private void connectServerNode(final InetSocketAddress remotePeer) {
        LOG.info("connect to server {}", remotePeer);

        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer());
                ChannelFuture channelFuture = b.connect(remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) {
                        if (channelFuture.isSuccess()) {
                            LOG.debug("Successfully connect to remote server. remote peer = {}", remotePeer);
                            NettyClientHandler handler = channelFuture.channel().pipeline().get(NettyClientHandler.class);
                            addHandler(handler);
                        }
                    }
                });
            }
        });
    }

    private void addHandler(NettyClientHandler handler) {
        connectedHandlers.add(handler);
        InetSocketAddress remoteAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
        connectedServerNodes.put(remoteAddress, handler);
        signalAvailableHandler();
    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            long connectTimeoutMillis = 6000;
            return connected.await(connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    public NettyClientHandler chooseHandler() {
        int size = connectedHandlers.size();
        while (isRuning && size <= 0) {
            try {
                boolean available = waitingForHandler();
                if (available) {
                    size = connectedHandlers.size();
                }
            } catch (Exception e) {
                LOG.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
        }
        int index = (roundRobin.getAndAdd(1) + size) % size;
        return connectedHandlers.get(index);
    }

    public void stop() {
        isRuning = false;
        for (int i = 0; i < connectedHandlers.size(); ++i) {
            NettyClientHandler connectedServerHandler = connectedHandlers.get(i);
            connectedServerHandler.close();
        }
        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    private static class SingletonHolder {
        private static final ConnectionManager INSTANCE = new ConnectionManager();
    }

}
