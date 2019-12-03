package org.miaohong.newfishchatserver.core.rpc.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.listen.StandardListenerManager;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.miaohong.newfishchatserver.core.rpc.registry.listener.ServiceCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class ServiceCache implements PathChildrenCacheListener, Listenable<ServiceCacheListener> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceCache.class);

    private StandardListenerManager<ServiceCacheListener> listenerManager = StandardListenerManager.standard();

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

        switch (event.getType()) {
            case CHILD_ADDED:
                LOG.info("addService");

//                            serviceObserver.addService(config, servicePath, event.getData(),
//                                    finalPathChildrenCache.getCurrentData());
                break;
            case CHILD_REMOVED:
//                            serviceObserver.removeService(config, servicePath, event.getData(),
//                                    finalPathChildrenCache.getCurrentData());
                break;
            case CHILD_UPDATED:
//                            serviceObserver.updateService(config, servicePath, event.getData(),
//                                    finalPathChildrenCache.getCurrentData());
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

    private void notifyListeners(ChildData data) {
        listenerManager.forEach((l) -> {
            l.onChange(data);
        });
    }

}
