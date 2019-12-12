package org.miaohong.newfishchatserver.core.rpc.register.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.listen.StandardListenerManager;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.miaohong.newfishchatserver.core.rpc.register.listener.ServiceCacheListener;
import org.miaohong.newfishchatserver.core.rpc.register.serializer.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class ZkServiceCache implements PathChildrenCacheListener, Listenable<ServiceCacheListener> {

    private static final Logger LOG = LoggerFactory.getLogger(ZkServiceCache.class);

    private StandardListenerManager<ServiceCacheListener> listenerManager = StandardListenerManager.standard();

    private String path;

    private JsonInstanceSerializer serializer = JsonInstanceSerializer.get();

    public ZkServiceCache(String path) {
        this.path = path;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        switch (event.getType()) {
            case CHILD_ADDED:
                LOG.info("addService");
                notifyListeners(event.getData(), true);
                break;
            case CHILD_REMOVED:
                notifyListeners(event.getData(), false);
                break;
            case CHILD_UPDATED:
                notifyListeners(event.getData(), false);
                break;
            default:
                break;

        }
    }

    @Override
    public void addListener(ServiceCacheListener listener) {
        listenerManager.addListener(listener);
    }

    @Override
    public void addListener(ServiceCacheListener listener, Executor executor) {
        listenerManager.addListener(listener, executor);
    }

    @Override
    public void removeListener(ServiceCacheListener listener) {
        listenerManager.removeListener(listener);
    }

    private void notifyListeners(ChildData data, boolean add) {
        listenerManager.forEach((l) -> {

            l.onChange(data, path, add, serializer);
        });
    }

}
