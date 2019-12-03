package org.miaohong.newfishchatserver.core.rpc.client;


import org.miaohong.newfishchatserver.core.rpc.network.client.config.ClientConfig;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClient;
import org.miaohong.newfishchatserver.core.rpc.registry.zk.ZookeeperRegistry;
import org.miaohong.newfishchatserver.core.util.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcClient<T> implements Client {

    private static final Logger LOG = LoggerFactory.getLogger(RpcClient.class);

    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.newCachedThreadPool(
            16, 16, ThreadPoolUtils.buildQueue(64));
    private NettyClient nettyClient;

    private ConsumerConfig<T> consumerConfig;

    private ConsumerBootstrap<T> consumerBootstrap;

    private ClientConfig clientConfig = new ClientConfig();

    public RpcClient(ConsumerConfig<T> consumerConfig) {
        this.consumerConfig = consumerConfig;
        this.consumerBootstrap = new ConsumerBootstrap<>(this.consumerConfig, new ZookeeperRegistry());
        this.nettyClient = new NettyClient(consumerConfig, clientConfig);
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

