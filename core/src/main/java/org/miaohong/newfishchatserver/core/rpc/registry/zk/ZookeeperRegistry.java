package org.miaohong.newfishchatserver.core.rpc.registry.zk;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.miaohong.newfishchatserver.core.execption.SystemCoreException;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.EventAction;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.ServiceRegistedEvent;
import org.miaohong.newfishchatserver.core.rpc.network.NetworkConfig;
import org.miaohong.newfishchatserver.core.rpc.network.server.config.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.registry.AbstractRegister;
import org.miaohong.newfishchatserver.core.rpc.registry.RegisterRole;
import org.miaohong.newfishchatserver.core.rpc.registry.RegistryPropConfig;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.JsonInstanceSerializer;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.ServiceInstance;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;


public class ZookeeperRegistry extends AbstractRegister implements UnhandledErrorListener {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private static final String CONTEXT_SEP = "/";
    private static final ConcurrentMap<ConsumerConfig, PathChildrenCache>
            INTERFACE_SERVICE_CACHE = Maps.newConcurrentMap();

    private static final List<ServiceConfig> SERVICE_CONFIG_LIST = Lists.newArrayList();
    private CuratorFramework zkClient;

    private JsonInstanceSerializer<ServerConfig> serializer = new JsonInstanceSerializer<>(ServerConfig.class);

    public ZookeeperRegistry() {
        super(RegistryPropConfig.get());
    }

    private void buildZkClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                registryPropConfig.getRetryWait(), registryPropConfig.getMaxRetryAttempts());
        CuratorFrameworkFactory.Builder zkClientuilder = CuratorFrameworkFactory.builder()
                .connectString(registryPropConfig.getAddress())
                .sessionTimeoutMs(registryPropConfig.getConnectTimeout())
                .connectionTimeoutMs(registryPropConfig.getTimeout())
                .canBeReadOnly(false)
                .retryPolicy(retryPolicy)
                .defaultData(null);

        zkClient = zkClientuilder.build();
    }

    private synchronized void init() {
        Preconditions.checkState(zkClient == null, "zk client already init");

        buildZkClient();

        zkClient.getUnhandledErrorListenable().addListener(this);

        zkClient.getConnectionStateListenable().addListener(
                (client, newState) -> handleConnectionStateChange(newState));
    }

    private void handleConnectionStateChange(ConnectionState newState) {
        switch (newState) {
            case CONNECTED:
                LOG.info("Connected to ZooKeeper quorum.");
                break;
            case SUSPENDED:
                LOG.info("Connection to ZooKeeper suspended.");
                break;
            case RECONNECTED:
                LOG.info("Connection to ZooKeeper was reconnected.");
                //// recoverRegistryData();
                break;
            case LOST:
                // Maybe we have to throw an exception here to terminate
                LOG.info("Connection to ZooKeeper lost.");
                break;
        }
    }

    @Override
    public void start(RegisterRole registerRole) {
        this.registerRole = registerRole;
        init();
        Preconditions.checkNotNull(zkClient, "Start zookeeper registry must be do init first!");
        if (zkClient.getState() == CuratorFrameworkState.STARTED) {
            LOG.info("zookeeper client already started");
            return;
        }
        try {
            zkClient.start();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new SystemCoreException("Failed to start zookeeper zkClient", e);
        }
        Preconditions.checkState(zkClient.getState() == CuratorFrameworkState.STARTED,
                "zookeeper registry not started!");
    }

    private String buildServicePath(String rootPath, ServiceConfig config) {
        return rootPath + config.getInterfaceId() + "/services";
    }

    private String buildServicePath(String rootPath, ConsumerConfig config) {
        return rootPath + config.getInterfaceId() + "/services";
    }


    @Override
    public void register(final ServiceConfig serviceConfig) {
        LOG.info("do register");
        NetworkConfig serverConfig = serviceConfig.getServerConfig();
        StringBuilder sb = new StringBuilder();
        String serverUrl = sb.append(buildServicePath(registryPropConfig.getRoot(), serviceConfig)).
                append(CONTEXT_SEP).append(serverConfig.getHost()).append(":")
                .append(serverConfig.getPort()).toString();

        try {
            getAndCheckZkClient().create().creatingParentsIfNeeded()
                    .withMode(getCreateMode(serviceConfig))
                    .forPath(serverUrl, serializer.serialize(ServiceInstance.<ServerConfig>builder(serviceConfig).
                            payload((ServerConfig) serviceConfig.getServerConfig()).build()));

            SERVICE_CONFIG_LIST.add(serviceConfig);
            LOG.info("start send event");
            serviceConfig.getEventBus().post(new ServiceRegistedEvent(EventAction.ADD,
                    serviceConfig.getInterfaceId(), serviceConfig.getRef()));
        } catch (KeeperException.NodeExistsException ignored) {
            SERVICE_CONFIG_LIST.add(serviceConfig);
            serviceConfig.getEventBus().post(new ServiceRegistedEvent(EventAction.ADD,
                    serviceConfig.getInterfaceId(), serviceConfig.getRef()));
            LOG.warn("service has exists in zookeeper, service={}", serverUrl);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new SystemCoreException(e.getMessage());
        }
    }

    @Override
    public void unRegister(final ServiceConfig serviceConfig) {
        LOG.info("do unRegister");
        NetworkConfig serverConfig = serviceConfig.getServerConfig();
        StringBuilder sb = new StringBuilder();
        String serverUrl = sb.append(buildServicePath(registryPropConfig.getRoot(), serviceConfig)).
                append(CONTEXT_SEP).append(serverConfig.getHost()).append(":")
                .append(serverConfig.getPort()).toString();

        LOG.info(sb.toString());

        try {
            getAndCheckZkClient().delete().deletingChildrenIfNeeded().forPath(serverUrl);
            SERVICE_CONFIG_LIST.remove(serviceConfig);
            serviceConfig.getEventBus().post(new ServiceRegistedEvent(EventAction.DEL,
                    serviceConfig.getInterfaceId(), serviceConfig.getRef()));
        } catch (KeeperException.NodeExistsException ignored) {
            LOG.warn("service has exists in zookeeper, service={}", serverUrl);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new SystemCoreException(e.getMessage());
        }

    }

    @Override
    public List<String> subscribe(final ConsumerConfig config) {
        List<String> servers = Lists.newArrayList();
        final String servicePath = buildServicePath(registryPropConfig.getRoot(), config);
        PathChildrenCache pathChildrenCache = INTERFACE_SERVICE_CACHE.get(config);
        if (pathChildrenCache == null) {
            pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
            pathChildrenCache.getListenable().addListener(new ServiceCache());
            try {
                pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new SystemCoreException(e.getMessage());
            }
            INTERFACE_SERVICE_CACHE.put(config, pathChildrenCache);
        }

        List<ChildData> childDatas = pathChildrenCache.getCurrentData();
        for (ChildData data : childDatas) {
            String server = data.getPath().substring(servicePath.length() + 1);
            servers.add(server);
        }

        return servers;
    }

    private CuratorFramework getAndCheckZkClient() {
        if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
            throw new SystemCoreException("Zookeeper client is not available");
        }
        return zkClient;
    }

    private void closePathChildrenCache(Map<ConsumerConfig, PathChildrenCache> map) {
        for (Map.Entry<ConsumerConfig, PathChildrenCache> entry : map.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                LOG.error("Close PathChildrenCache error!", e);
                throw new SystemCoreException(e.getMessage());
            }
        }
    }

    @Override
    public void destroy() {
        LOG.info("register do destory");
        if (!SERVICE_CONFIG_LIST.isEmpty()) {
            SERVICE_CONFIG_LIST.forEach(this::unRegister);
        }

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

    @Override
    public void unhandledError(String message, Throwable e) {
        registerRole.handleError(new SystemCoreException("Unhandled error : " + message, e));
    }
}
