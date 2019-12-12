package org.miaohong.newfishchatserver.core.rpc.proto;

import lombok.Data;

@Data
public class RpcResponse {

    private boolean isError = false;
    private String requestId;
    private String errorMsg;
    private Object result;

}
