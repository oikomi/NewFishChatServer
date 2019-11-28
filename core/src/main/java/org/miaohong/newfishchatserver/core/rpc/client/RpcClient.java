package org.miaohong.newfishchatserver.core.rpc.client;


import org.miaohong.newfishchatserver.core.rpc.client.transport.NettyClient;
import org.miaohong.newfishchatserver.core.rpc.registry.zk.ZookeeperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient<T> implements Client {

    private static final Logger LOG = LoggerFactory.getLogger(RpcClient.class);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65535));

    private NettyClient nettyClient;

    private ConsumerConfig<T> consumerConfig;

    private ConsumerBootstrap<T> consumerBootstrap;

    public RpcClient(ConsumerConfig<T> consumerConfig) {
        this.consumerConfig = consumerConfig;
        this.consumerBootstrap = new ConsumerBootstrap<>(this.consumerConfig, new ZookeeperRegistry());
        this.nettyClient = new NettyClient(consumerConfig);
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public T refer() {
        LOG.info("client do refer");
        return consumerBootstrap.refer();
    }

    public void stop() {
        threadPoolExecutor.shutdown();
        ConnectionManager.getINSTANCE().stop();
    }

    @Override
    public void start() {
        nettyClient.start(new InetSocketAddress("127.0.0.1", 15000));
    }

    @Override
    public void shutDown() {
        stop();
    }

}

