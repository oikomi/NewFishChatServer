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
import java.util.UUID;

@Internal
public class JDKInvocationHandler<T> extends AbstractInvocationHandler implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JDKInvocationHandler.class);
    private Class<T> clazz;

    private ServiceStrategy serviceStrategy;

    public JDKInvocationHandler(Class<T> clazz, ServiceStrategy serviceStrategy) {
        this.clazz = clazz;
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
        LOG.debug(method.getDeclaringClass().getName());
        LOG.debug(method.getName());

        LOG.info("send rpc");

        Thread.sleep(2000);

        ServiceInstance serviceInstance = serviceStrategy.getInstance();

        LOG.info("serviceInstance : {}", serviceInstance);
        NettyClientHandler handler = serviceStrategy.getNettyClientHandler(
                serviceInstance.getHost() + ":" + serviceInstance.getPort());

        LOG.info("choose handler");

        RPCFuture rpcFuture = handler.sendRequest(request);

        return rpcFuture.get();
    }

//    @Override
//    public RPCFuture call(String funcName, Object... args) {
//        NettyClientHandler handler = ConnectionManager.getINSTANCE().chooseHandler();
//        RpcRequest request = createRequest(this.clazz.getName(), funcName, args);
//        return handler.sendRequest(request);
//    }

    private RpcRequest createRequest(String className, String methodName, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceId(className);
        request.setMethodName(methodName);
        request.setParameters(args);

        Class[] parameterTypes = new Class[args.length];
        // Get the right class type
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
//        Method[] methods = clazz.getDeclaredMethods();
//        for (int i = 0; i < methods.length; ++i) {
//            // Bug: if there are 2 methods have the same name
//            if (methods[i].getName().equals(methodName)) {
//                parameterTypes = methods[i].getParameterTypes();
//                request.setParameterTypes(parameterTypes); // get parameter types
//                break;
//            }
//        }

        LOG.debug(className);
        LOG.debug(methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            LOG.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < args.length; ++i) {
            LOG.debug(args[i].toString());
        }

        return request;
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }

}
