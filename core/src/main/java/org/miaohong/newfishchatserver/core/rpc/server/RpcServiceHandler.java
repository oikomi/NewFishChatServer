package org.miaohong.newfishchatserver.core.rpc.server;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Data
public class RpcServiceHandler implements IServiceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RpcServiceHandler.class);

    private final Map<String, Object> serviceMap = new HashMap<>();

    @Override
    public void add(String interfaceName, Object serviceBean) {
        if (!serviceMap.containsKey(interfaceName)) {
            LOG.info("Loading service: {}", interfaceName);
            serviceMap.put(interfaceName, serviceBean);
        }
    }

    @Override
    public Object get(String className) {
        return serviceMap.get(className);
    }

}