package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Strings;
import lombok.Data;
import org.miaohong.newfishchatserver.core.execption.ClientCoreException;
import org.miaohong.newfishchatserver.core.lb.strategy.AbstractServiceStrategy;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBus;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBusManager;
import org.miaohong.newfishchatserver.core.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T>
 */
@Data
public class ConsumerConfig<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerConfig.class);
    private final EventBus eventBus = EventBusManager.get();
    protected Class<T> proxyClass;
    private String proxy = ProxyConstants.PROXY_BYTEBUDDY;
    private String interfaceId;

    public ConsumerConfig() {
        eventBus.register(new AbstractServiceStrategy.RpcClientHandlerListener());
    }

    @SuppressWarnings("unchecked")
    Class<T> getProxyClass() {
        if (proxyClass != null) {
            return proxyClass;
        }
        try {
            LOG.info(interfaceId);
            if (!Strings.isNullOrEmpty(interfaceId)) {
                proxyClass = ClassUtils.forName(interfaceId);
                if (!proxyClass.isInterface()) {
                    throw new ClientCoreException("interfaceId must set interface class, not implement class");
                }
            } else {
                throw new ClientCoreException("interfaceId must be not null");
            }
        } catch (RuntimeException e) {
            throw new ClientCoreException(e.getMessage(), e);
        }
        return proxyClass;
    }

}
