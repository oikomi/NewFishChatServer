package org.miaohong.newfishchatserver.core.rpc.registry.listener;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.miaohong.newfishchatserver.core.rpc.registry.serializer.InstanceSerializer;

public interface ServiceCacheListener {

    void onChange(ChildData data, String path, boolean add, InstanceSerializer serializer);
}
