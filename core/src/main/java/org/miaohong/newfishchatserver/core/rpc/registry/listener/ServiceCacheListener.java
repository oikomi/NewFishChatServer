package org.miaohong.newfishchatserver.core.rpc.registry.listener;

import org.apache.curator.framework.recipes.cache.ChildData;

public interface ServiceCacheListener {

    void onChange(ChildData data);
}
