package org.miaohong.newfishchatserver.core.rpc.client.cluster;

import org.miaohong.newfishchatserver.core.rpc.registry.zk.ServiceListener;

import java.util.List;

public abstract class ClusterListener implements ServiceListener {

    private List<String> servers;

    public ClusterListener() {

    }

    @Override
    public void addService() {

    }
}
