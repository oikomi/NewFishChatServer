package org.miaohong.newfishchatserver.core.rpc.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient<T> implements Client {

    private static final Logger LOG = LoggerFactory.getLogger(RpcClient.class);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65535));

    private String serverAddress;

    private ConsumerConfig<T> consumerConfig;

    private ConsumerBootstrap<T> consumerBootstrap;

    public RpcClient(String serverAddress, ConsumerConfig<T> consumerConfig) {
        this.serverAddress = serverAddress;
        this.consumerConfig = consumerConfig;
        this.consumerBootstrap = new ConsumerBootstrap<>(this.consumerConfig);
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
        ConnectManager.getInstance().stop();
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {
        stop();
    }
}

