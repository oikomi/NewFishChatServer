package org.miaohong.newfishchatserver.core.rpc.client;


import org.miaohong.newfishchatserver.core.rpc.client.proxy.CallerInvocationHandler;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.IAsyncObjectProxy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.JdkProxyFactory;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient implements Client {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    private String serverAddress;

    private ProxyFactory proxyFactory = new JdkProxyFactory();

    public RpcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> interfaceClass) {
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new CallerInvocationHandler<>(interfaceClass);
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

