package org.miaohong.newfishchatserver.core.rpc.registry.zk;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.ServiceRegistedEvent;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;
import org.miaohong.newfishchatserver.core.rpc.registry.RegistryPropConfig;
import org.miaohong.newfishchatserver.core.rpc.server.config.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ZookeeperRegistry extends Register {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private static final String CONTEXT_SEP = "/";
    private final static byte[] PROVIDER_OFFLINE = new byte[]{0};
    private final static byte[] PROVIDER_ONLINE = new byte[]{1};
    private static final ConcurrentMap<ConsumerConfig, PathChildrenCache> INTERFACE_SERVICE_CACHE = new ConcurrentHashMap<>();
    private CuratorFramework zkClient;
    private String rootPath = "/fishchatserver/rpc/";
    private boolean ephemeralNode = true;
    private ServiceObserver serviceObserver;

    public ZookeeperRegistry() {
        super(RegistryPropConfig.getINSTANCE());
    }

    public static void main(String[] args) {
        new ZookeeperRegistry().start();
    }

    private synchronized void init() {
        if (zkClient != null) {
            return;
        }
        String addressInput = registryPropConfig.getAddress();
        if (Strings.isNullOrEmpty(addressInput)) {
            throw new RuntimeException("Address of zookeeper registry is empty.");
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFrameworkFactory.Builder zkClientuilder = CuratorFrameworkFactory.builder()
                .connectString(registryPropConfig.getAddress())
                .sessionTimeoutMs(registryPropConfig.getConnectTimeout())
                .connectionTimeoutMs(registryPropConfig.getTimeout())
                .canBeReadOnly(false)
                .retryPolicy(retryPolicy)
                .defaultData(null);

        zkClient = zkClientuilder.build();

        LOG.info(zkClient.getState().name());

        zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

                if (LOG.isInfoEnabled()) {
                    LOG.info("reconnect to zookeeper,recover provider and consumer data");
                }
                if (newState == ConnectionState.RECONNECTED) {
//                    recoverRegistryData();
                }
            }
        });
    }

    @Override
    public boolean start() {
        init();
        if (zkClient == null) {
            LOG.warn("Start zookeeper registry must be do init first!");
            return false;
        }
        if (zkClient.getState() == CuratorFrameworkState.STARTED) {
            return true;
        }
        try {
            zkClient.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start zookeeper zkClient", e);
        }
        return zkClient.getState() == CuratorFrameworkState.STARTED;
    }

    private String buildServicerPath(String rootPath, ServiceConfig config) {
        return rootPath + config.getInterfaceId() + "/services";
    }

    private String buildServicerPath(String rootPath, ConsumerConfig config) {
        return rootPath + config.getInterfaceId() + "/services";
    }

    @Override
    public void register(ServiceConfig serviceConfig) {
        LOG.info("do register");
        ServerConfig serverConfig = serviceConfig.getServerConfig();
        StringBuilder sb = new StringBuilder();
        String serverUrl = sb.append(buildServicerPath(rootPath, serviceConfig)).
                append(CONTEXT_SEP).append(serverConfig.getHost()).append(":")
                .append(serverConfig.getPort()).toString();

        LOG.info(sb.toString());

        try {
            getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                    .withMode(ephemeralNode ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT)
                    .forPath(serverUrl, PROVIDER_ONLINE);
            serviceConfig.getEventBus().post(new ServiceRegistedEvent(
                    serviceConfig.getInterfaceId(), serviceConfig.getRef()));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<String> subscribe(ConsumerConfig config) {
        List<String> servers = Lists.newArrayList();
        if (serviceObserver == null) {
            serviceObserver = new ZookeeperServiceObserver();
        }
        final String servicePath = buildServicerPath(rootPath, config);
        PathChildrenCache pathChildrenCache = INTERFACE_SERVICE_CACHE.get(config);
        if (pathChildrenCache == null) {
            pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
            final PathChildrenCache finalPathChildrenCache = pathChildrenCache;
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client1, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            LOG.info("addService");
                            serviceObserver.addService(config, servicePath, event.getData(),
                                    finalPathChildrenCache.getCurrentData());
                            break;
                        case CHILD_REMOVED:
                            serviceObserver.removeService(config, servicePath, event.getData(),
                                    finalPathChildrenCache.getCurrentData());
                            break;
                        case CHILD_UPDATED:
                            serviceObserver.updateService(config, servicePath, event.getData(),
                                    finalPathChildrenCache.getCurrentData());
                            break;
                        default:
                            break;
                    }
                }
            });
            try {
                pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            INTERFACE_SERVICE_CACHE.put(config, pathChildrenCache);
        }

        LOG.info(String.valueOf(pathChildrenCache.getCurrentData()));

        List<ChildData> childDatas = pathChildrenCache.getCurrentData();

        for (ChildData data : childDatas) {
            String server = data.getPath().substring(servicePath.length() + 1);
            servers.add(server);
        }

        return servers;

    }

    private CuratorFramework getAndCheckZkClient() {
        if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
            throw new RuntimeException("Zookeeper client is not available");
        }
        return zkClient;
    }

    private void closePathChildrenCache(Map<ConsumerConfig, PathChildrenCache> map) {
        for (Map.Entry<ConsumerConfig, PathChildrenCache> entry : map.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                LOG.error("Close PathChildrenCache error!", e);
            }
        }
    }

    @Override
    public void destroy() {
        closePathChildrenCache(INTERFACE_SERVICE_CACHE);
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            zkClient.close();
        }
    }

    @Override
    public void destroy(DestroyHook hook) {
        hook.preDestroy();
        destroy();
        hook.postDestroy();
    }
}
