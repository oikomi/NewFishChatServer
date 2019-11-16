package org.miaohong.newfishchatserver.core.rpc.client.proxy.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.miaohong.newfishchatserver.core.rpc.client.ConnectManager;
import org.miaohong.newfishchatserver.core.rpc.client.RPCFuture;
import org.miaohong.newfishchatserver.core.rpc.client.RpcClientHandler;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.AbstractInvocationHandler;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public class BytebuddyInvocationHandler extends AbstractInvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BytebuddyInvocationHandler.class);

    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args)
            throws ExecutionException, InterruptedException {

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
        LOG.debug(method.getDeclaringClass().getName());
        LOG.debug(method.getName());

        RpcClientHandler handler = ConnectManager.getInstance().chooseHandler();
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture.get();
    }
}