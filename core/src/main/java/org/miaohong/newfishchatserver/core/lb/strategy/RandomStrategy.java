package org.miaohong.newfishchatserver.core.lb.strategy;


import org.miaohong.newfishchatserver.core.rpc.network.client.transport.NettyClientHandler;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

public class RandomStrategy<T> extends AbstractServiceStrategy<T> implements ServiceStrategy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RandomStrategy.class);

    private final Random random = new Random();

    public RandomStrategy() {
        super();
    }

    @Override
    public ServiceInstance<T> getInstance() {
        List<ServiceInstance<T>> instances = instanceProvider.getInstances();
        if (CollectionUtils.isEmpty(instances)) {
            return null;
        }
        int thisIndex = random.nextInt(instances.size());
        return instances.get(thisIndex);
    }

    @Override
    public NettyClientHandler getNettyClientHandler(String serverAddr) {
        LOG.info("nettyClientHandlers : {}", nettyClientHandlers);
        return nettyClientHandlers.get(serverAddr);
    }


}
