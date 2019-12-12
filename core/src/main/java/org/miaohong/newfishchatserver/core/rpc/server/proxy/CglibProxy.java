package org.miaohong.newfishchatserver.core.rpc.server.proxy;

import net.sf.cglib.reflect.FastClass;
import org.miaohong.newfishchatserver.core.execption.CoreErrorMsg;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CglibProxy {

    private static final Logger LOG = LoggerFactory.getLogger(CglibProxy.class);

    public static Object invoke(Class<?> serviceClass, String methodName, Class<?>[] parameterTypes,
                                Object serviceBean, Object[] parameters) {
        try {
            FastClass serviceFastClass = FastClass.create(serviceClass);
            int methodIndex = serviceFastClass.getIndex(methodName, parameterTypes);
            return serviceFastClass.invoke(methodIndex, serviceBean, parameters);
        } catch (Exception e) {
            LOG.error("CglibProxy invoke failed {}", e.getMessage(), e);
            throw new ServerCoreException(new CoreErrorMsg(-10, 1003, "CglibProxy invoke failed"));
        }
    }

}
