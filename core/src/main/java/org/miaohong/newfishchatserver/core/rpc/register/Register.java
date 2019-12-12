package org.miaohong.newfishchatserver.core.rpc.register;

import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;


public interface Register {


    void start(RegisterRole registerRole);

    void register(final ServiceConfig config);

    void unRegister(final ServiceConfig config);

    void subscribe(final ConsumerConfig config);

    void unSubscribe();

}
