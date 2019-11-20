package org.miaohong.newfishchatserver.core.rpc.server.proxy;

import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class JdkProxy {

    private static final Logger LOG = LoggerFactory.getLogger(JdkProxy.class);

    public static Object invoke(Class<?> serviceClass, String methodName, Class<?>[] parameterTypes,
                                Object serviceBean, Object[] parameters) {
        try {
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, parameters);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServerCoreException(e.getMessage());
        }
    }
}
