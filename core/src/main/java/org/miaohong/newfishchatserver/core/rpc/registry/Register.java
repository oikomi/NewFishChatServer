package org.miaohong.newfishchatserver.core.rpc.registry;

import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;

import java.util.List;

public interface Register {


    void start(RegisterRole registerRole);

    void register(final ServiceConfig config);

    void unRegister(final ServiceConfig config);

    List<String> subscribe(final ConsumerConfig config);

    void unSubscribe();

}
