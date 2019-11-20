package org.miaohong.newfishchatserver.core.rpc.client.proxy;

import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;

import java.lang.reflect.Method;
import java.util.UUID;

public abstract class AbstractInvocationHandler {

    protected boolean isLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    protected RpcRequest buildRequest(Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceId(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        return request;
    }

}
