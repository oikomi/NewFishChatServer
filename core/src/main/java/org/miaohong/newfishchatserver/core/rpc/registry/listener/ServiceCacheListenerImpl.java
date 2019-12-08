package org.miaohong.newfishchatserver.core.rpc.registry.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.miaohong.newfishchatserver.core.lb.strategy.InstanceProvider;
import org.miaohong.newfishchatserver.core.rpc.client.NettyClientFactory;
import org.miaohong.newfishchatserver.core.rpc.concurrency.NamedThreadFactory;
import org.miaohong.newfishchatserver.core.rpc.network.client.config.ClientConfig;
import org.miaohong.newfishchatserver.core.rpc.network.server.config.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.InstanceSerializer;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.ServiceInstance;
import org.miaohong.newfishchatserver.core.util.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ServiceCacheListenerImpl implements ServiceCacheListener, InstanceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceCacheListenerImpl.class);

    private static ConcurrentMap<String, ServiceInstance> instances = Maps.newConcurrentMap();

    private ThreadPoolExecutor threadExecutor = getExecutor();

    private ServiceCacheListenerImpl() {
    }

    public static ServiceCacheListenerImpl get() {
        return ServiceCacheListenerImpl.SingletonHolder.INSTANCE;
    }

    private ThreadPoolExecutor getExecutor() {
        RejectedExecutionHandler handler = (Runnable r, ThreadPoolExecutor executor) -> {
            LOG.error("Task:{} has been reject because of threadPool exhausted!" +
                            " pool:{}, active:{}, queue:{}, taskcnt: {}", r,
                    executor.getPoolSize(),
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getTaskCount());
            throw new RejectedExecutionException("Callback handler thread pool has bean exhausted");
        };

        return ThreadPoolUtils.newCachedThreadPool(
                4,
                64,
                1000,
                ThreadPoolUtils.buildQueue(128),
                new NamedThreadFactory("client handler"), handler);
    }


    @Override
    public List<ServiceInstance> getInstances() {
        List<ServiceInstance> res = Lists.newArrayList();
        instances.forEach((k, v) -> {
            res.add(v);
        });

        return res;
    }

    @Override
    public void onChange(ChildData data, String path, boolean add, InstanceSerializer<ServerConfig> serializer) {
        String server = data.getPath().substring(path.length() + 1);
        LOG.info("instances : {}", instances);
        if (add) {
            try {
                LOG.info(new String(data.getData()));
                ServiceInstance<ServerConfig> serviceInstance = serializer.deserialize(data.getData());
                instances.put(server, serviceInstance);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            String[] strs = server.split(":");
            threadExecutor.submit(NettyClientFactory.getClient(
                    new ClientConfig(strs[0], Integer.parseInt(strs[1]))));
        } else {
            instances.remove(server);
        }

    }


    private static class SingletonHolder {
        private static final ServiceCacheListenerImpl INSTANCE = new ServiceCacheListenerImpl();
    }

}
