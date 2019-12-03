package org.miaohong.newfishchatserver.core.rpc.registry.listener;

import org.apache.curator.framework.recipes.cache.ChildData;

public class ServiceCacheListenerImpl implements ServiceCacheListener {
    @Override
    public void onChange(ChildData data) {
        data.getData();

    }
}
