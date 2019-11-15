package org.miaohong.newfishchatserver.core.rpc.client;


import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.IAsyncObjectProxy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyFactory;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.jdk.JDKInvocationHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient implements Client {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65535));
    private String serverAddress;

    private ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(ProxyFactory.class,
            ProxyConstants.PROXY_BYTEBUDDY);

    public RpcClient(String serverAddress) throws InstantiationException, IllegalAccessException {
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
        return new JDKInvocationHandler<>(interfaceClass);
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

