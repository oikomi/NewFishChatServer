package org.miaohong.newfishchatserver.core.rpc.server;

public interface IServiceHandler {

    Object get(String className);

    void add(String interfaceName, Object serviceBean);

}
