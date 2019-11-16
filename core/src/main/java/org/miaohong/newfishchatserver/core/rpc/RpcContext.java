package org.miaohong.newfishchatserver.core.rpc;

import lombok.Getter;
import lombok.Setter;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;

import java.util.HashMap;
import java.util.Map;

public class RpcContext {

    private static final ThreadLocal<RpcContext> LOCAL_CONTEXT = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    private Map<Object, Object> attributes = new HashMap<>();
    private Map<String, String> attachments = new HashMap<>();// attachment in rpc context. not same with request's attachments

    @Getter
    @Setter
    private RpcRequest request;

    @Getter
    @Setter
    private RpcResponse response;

    @Getter
    @Setter
    private String clientRequestId = null;

    public static RpcContext getContext() {
        return LOCAL_CONTEXT.get();
    }

    public static RpcContext init(RpcRequest request) {
        RpcContext context = new RpcContext();
        if (request != null) {
            context.setRequest(request);
//            context.setClientRequestId(request.getAttachments().get(URLParamType.requestIdFromClient.getName()));
        }
        LOCAL_CONTEXT.set(context);
        return context;
    }

    public static RpcContext init() {
        RpcContext context = new RpcContext();
        LOCAL_CONTEXT.set(context);
        return context;
    }

    public static void destroy() {
        LOCAL_CONTEXT.remove();
    }

}
