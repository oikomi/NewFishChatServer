package org.miaohong.newfishchatserver.core.rpc.proto;

import lombok.Data;

@Data
public class RpcRequest {

    private String requestId;
    private String interfaceId;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}