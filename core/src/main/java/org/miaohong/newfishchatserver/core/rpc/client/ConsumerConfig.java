package org.miaohong.newfishchatserver.core.rpc.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.miaohong.newfishchatserver.core.execption.ClientCoreException;
import org.miaohong.newfishchatserver.core.lb.strategy.AbstractServiceStrategy;
import org.miaohong.newfishchatserver.core.lb.strategy.StrategyConstants;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.ProxyConstants;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBus;
import org.miaohong.newfishchatserver.core.rpc.eventbus.EventBusManager;
import org.miaohong.newfishchatserver.core.rpc.registry.RegisterConstants;
import org.miaohong.newfishchatserver.core.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerConfig<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerConfig.class);

    private final EventBus eventBus = EventBusManager.get();

    protected Class<T> proxyClass;

    @Getter
    private String proxy = ProxyConstants.PROXY_JDK;

    @Getter
    private String register = RegisterConstants.REGISTER_ZOOKEEPER;

    @Getter
    private String strategy = StrategyConstants.STRATEGY_RANDOM;

    @Getter
    @Setter
    private String interfaceId;

    public ConsumerConfig() {
        eventBus.register(new AbstractServiceStrategy.RpcClientHandlerListener());
    }

    @SuppressWarnings("unchecked")
    public Class<T> getProxyClass() {
        if (proxyClass != null) {
            return proxyClass;
        }
        try {
            if (!Strings.isNullOrEmpty(interfaceId)) {
                proxyClass = ClassUtils.forName(interfaceId);
                Preconditions.checkNotNull(proxyClass);
                if (!proxyClass.isInterface()) {
                    throw new ClientCoreException("interfaceId must set interface class, not implement class");
                }
            } else {
                LOG.error("interfaceId is null");
                throw new ClientCoreException("interfaceId must be not null");
            }
        } catch (Exception e) {
            throw new ClientCoreException(e.getMessage(), e);
        }
        return proxyClass;
    }

}
