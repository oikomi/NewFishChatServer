package org.miaohong.newfishchatserver.core.rpc.network.server.transport.handler;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.miaohong.newfishchatserver.core.conf.prop.CommonNettyPropConfig;
import org.miaohong.newfishchatserver.core.execption.ServerCoreException;
import org.miaohong.newfishchatserver.core.rpc.RpcContext;
import org.miaohong.newfishchatserver.core.rpc.RpcHandler;
import org.miaohong.newfishchatserver.core.rpc.channel.NettyChannel;
import org.miaohong.newfishchatserver.core.rpc.concurrency.NamedThreadFactory;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.ServiceRegistedEvent;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.proto.RpcResponse;
import org.miaohong.newfishchatserver.core.rpc.server.proxy.CglibProxy;
import org.miaohong.newfishchatserver.core.util.NetUtils;
import org.miaohong.newfishchatserver.core.util.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


@io.netty.channel.ChannelHandler.Sharable
public class NettyServerMessageHandler extends SimpleChannelInboundHandler<RpcRequest> implements RpcHandler {

    public static final String NAME = "message handler";
    private static final Logger LOG = LoggerFactory.getLogger(NettyServerMessageHandler.class);
    private static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();
    private CommonNettyPropConfig nettyPropConfig;
    private org.miaohong.newfishchatserver.core.rpc.channel.Channel channel;

    private ListeningExecutorService executor;

    public NettyServerMessageHandler(CommonNettyPropConfig nettyPropConfig) {
        LOG.info("enter NettyServerMessageHandler");
        this.nettyPropConfig = nettyPropConfig;
        this.executor = MoreExecutors.listeningDecorator(getExecutor());
    }

    private ThreadPoolExecutor getExecutor() {
        RejectedExecutionHandler handler = (Runnable r, ThreadPoolExecutor executor) -> {
            LOG.error("Task:{} has been reject because of threadPool exhausted!" +
                            " pool:{}, active:{}, queue:{}, taskcnt: {}", r,
                    executor.getPoolSize(),
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getTaskCount());
            throw new RejectedExecutionException("Callback handler thread pool has bean exhausted");
        };

        return ThreadPoolUtils.newCachedThreadPool(
                nettyPropConfig.getNettyServerPoolCore(),
                nettyPropConfig.getNettyServerPoolMax(),
                nettyPropConfig.getNettyServerPoolAlive(),
                ThreadPoolUtils.buildQueue(nettyPropConfig.getNettyServerPoolQueue()),
                new NamedThreadFactory("server message handler"), handler);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = new NettyChannel(ctx);
        LOG.info("client register {}", ctx.channel().remoteAddress());
        channels.put(NetUtils.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOG.info("client unregister {}", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    private RpcResponse buildResponse(String requestId, boolean isSuccess, Object result, String errMsg) {

        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);

        if (isSuccess) {
            response.setResult(result);
        } else {
            response.setError(true);
            response.setErrorMsg(errMsg);
        }

        return response;
    }

    private void processReq(final RpcRequest request) throws ExecutionException, InterruptedException {
        LOG.info("Receive request {}", request.getRequestId());
        LOG.info("client set {}", channels);

        String requestId = request.getRequestId();

        ListenableFuture<Object> explosion = executor.submit(() -> handle(request));
        Futures.addCallback(explosion, new FutureCallback<Object>() {
            // we want this handler to run immediately after we push the big red button!
            @Override
            public void onSuccess(Object explosion) {
                LOG.info("onSuccess");
                RpcResponse response = buildResponse(requestId, true, explosion, null);
                channel.writeAndFlush(response);
            }

            @Override
            public void onFailure(Throwable e) {
                LOG.info("onFailure");
                RpcResponse response = buildResponse(requestId, false, null, e.getMessage());
                LOG.error("RPC Server handle request error {}", e.getMessage(), e);
                channel.writeAndFlush(response);
            }
        }, getExecutor());
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final RpcRequest request) {
        try {
            processReq(request);
        } catch (ExecutionException e) {
            LOG.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private Object handle(final RpcRequest request) {
        RpcContext.init(request);
        String interfaceId = request.getInterfaceId();
        Object serviceBean = SERVICE_MAP.get(interfaceId);
        if (serviceBean == null) {
            throw new ServerCoreException("serviceBean is null");
        }
        LOG.info("serviceBean: {}", serviceBean);
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        return CglibProxy.invoke(serviceClass, methodName, parameterTypes, serviceBean, parameters);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("server caught exception", cause);
        ctx.close();
    }

    public static class RpcServerHandlerListener {
        @Subscribe
        public void doAction(final Object event) {
            LOG.info("Received event [{}] and will take a action", event);
            if (event instanceof ServiceRegistedEvent) {
                ServiceRegistedEvent serviceRegistedEvent = (ServiceRegistedEvent) event;
                if (serviceRegistedEvent.getAction().isAddState()) {
                    SERVICE_MAP.put(serviceRegistedEvent.getInterfaceId(), serviceRegistedEvent.getRef());
                } else if (serviceRegistedEvent.getAction().isDelState()) {
                    SERVICE_MAP.remove(serviceRegistedEvent.getInterfaceId());
                }
            }
        }
    }
}
