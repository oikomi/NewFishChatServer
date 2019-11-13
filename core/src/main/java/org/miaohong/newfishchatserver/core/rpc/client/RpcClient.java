package org.miaohong.newfishchatserver.core.rpc.client;


import org.miaohong.newfishchatserver.core.rpc.client.proxy.IAsyncObjectProxy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ObjectProxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient implements Client {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    private String serverAddress;

    public RpcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<>(interfaceClass)
        );
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<>(interfaceClass);
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

