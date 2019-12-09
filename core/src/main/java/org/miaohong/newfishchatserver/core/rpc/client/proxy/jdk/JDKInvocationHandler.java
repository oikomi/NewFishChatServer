package org.miaohong.newfishchatserver.core.rpc.client.proxy.jdk;

import org.miaohong.newfishchatserver.annotations.Internal;
import org.miaohong.newfishchatserver.core.lb.strategy.ServiceStrategy;
import org.miaohong.newfishchatserver.core.rpc.client.RPCFuture;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.AbstractInvocationHandler;
import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Internal
public class JDKInvocationHandler<T> extends AbstractInvocationHandler implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JDKInvocationHandler.class);

    private ServiceStrategy serviceStrategy;

    public JDKInvocationHandler(ServiceStrategy serviceStrategy) {
        this.serviceStrategy = serviceStrategy;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isLocalMethod(method)) {
            String name = method.getName();
            Class[] paramTypes = method.getParameterTypes();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = buildRequest(method, args);
        LOG.info("send rpc");

        Thread.sleep(2000);

        ServiceInstance serviceInstance = serviceStrategy.getInstance();

        LOG.info("serviceInstance : {}", serviceInstance);
        NettyClientHandler handler = serviceStrategy.getNettyClientHandler(
                serviceInstance.getServerAddr());

        LOG.info("choose handler");

        RPCFuture rpcFuture = handler.sendRequest(request);

        return rpcFuture.get();
    }

}
