package org.miaohong.newfishchatserver.core.rpc.server.transport.handler;

import com.google.common.eventbus.Subscribe;
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

    private ThreadPoolExecutor threadExecutor;

    public NettyServerMessageHandler(CommonNettyPropConfig nettyPropConfig) {
        LOG.info("enter NettyServerMessageHandler");
        this.nettyPropConfig = nettyPropConfig;
        this.threadExecutor = getExecutor();
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

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final RpcRequest request) {
        threadExecutor.submit(() -> {
            LOG.info("Receive request {}", request.getRequestId());
            LOG.info("client set {}", channels);

            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            try {
                Object result = handle(request);
                response.setResult(result);
            } catch (Exception e) {
                response.setError("failed");
                LOG.error("RPC Server handle request error", e);
            }

            LOG.info("start to send response");
            channel.writeAndFlush(response);
        });
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

        LOG.info(serviceClass.getName());
        LOG.info(methodName);

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
