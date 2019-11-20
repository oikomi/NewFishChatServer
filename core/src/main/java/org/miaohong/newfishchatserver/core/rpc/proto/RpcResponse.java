package org.miaohong.newfishchatserver.core.rpc.proto;

import lombok.Data;

@Data
public class RpcResponse {
    private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }
}
