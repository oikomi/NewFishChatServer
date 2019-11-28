package org.miaohong.newfishchatserver.core.runtime;

import com.google.common.collect.Sets;
import org.miaohong.newfishchatserver.core.rpc.base.Destroyable;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerBootstrap;
import org.miaohong.newfishchatserver.core.rpc.service.ServiceBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RuntimeContext {

    /**
     * 当前进程Id
     */
    public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    /**
     * 当前应用启动时间（用这个类加载时间为准）
     */
    public static final long START_TIME = now();
    private static final Logger LOG = LoggerFactory.getLogger(RuntimeContext.class);
    /**
     * 上下文信息，例如instancekey，本机ip等信息
     */
    private final static ConcurrentMap CONTEXT = new ConcurrentHashMap();
    /**
     * 发布的服务配置
     */
    private static final Set<ServiceBootstrap> EXPORTED_SERVICE_CONFIGS = Sets.newConcurrentHashSet();

    /**
     * 发布的订阅配置
     */
    private static final Set<ConsumerBootstrap> REFERRED_CONSUMER_CONFIGS = Sets.newConcurrentHashSet();

    /**
     * 关闭资源的钩子
     */
    private static final List<Destroyable.DestroyHook> DESTROY_HOOKS = new CopyOnWriteArrayList<>();

    static {
        if (LOG.isInfoEnabled()) {
            LOG.info("Welcome! Loading RPC Framework , PID is:{}", PID);
        }
        // 初始化一些上下文
        initContext();
        // 初始化其它模块
        // 增加jvm关闭事件
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("RPC Framework catch JVM shutdown event, Run shutdown hook now.");
                }
                destroy();
            }
        }, "RPC-ShutdownHook"));

    }

    /**
     * 初始化一些上下文
     */
    private static void initContext() {
    }

    private static void destroy() {
        for (Destroyable.DestroyHook destroyHook : DESTROY_HOOKS) {
            destroyHook.preDestroy();
        }
        for (ServiceBootstrap bootstrap : EXPORTED_SERVICE_CONFIGS) {
            bootstrap.unExport();
            bootstrap.destroy();
        }
        // 关闭调用的服务
        for (ConsumerBootstrap bootstrap : REFERRED_CONSUMER_CONFIGS) {
            bootstrap.destroy();
        }
    }

    /**
     * 注册销毁器
     *
     * @param destroyHook 结果
     */
    public static void registryDestroyHook(Destroyable.DestroyHook destroyHook) {
        DESTROY_HOOKS.add(destroyHook);
    }

    /**
     * 获取当前时间，此处可以做优化
     *
     * @return 当前时间
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * 增加缓存ConsumerConfig
     *
     * @param consumerConfig the consumer config
     */
    public static void cacheConsumerBootstrap(ConsumerBootstrap consumerConfig) {
        REFERRED_CONSUMER_CONFIGS.add(consumerConfig);
    }

    public static void cacheServiceBootstrap(ServiceBootstrap serviceBootstrap) {
        EXPORTED_SERVICE_CONFIGS.add(serviceBootstrap);
    }

    /**
     * 得到上下文信息
     *
     * @param key the key
     * @return the object
     * @see ConcurrentHashMap#get(Object)
     */
    public static Object get(String key) {
        return CONTEXT.get(key);
    }

    /**
     * 设置上下文信息（不存在才设置成功）
     *
     * @param key   the key
     * @param value the value
     * @return the object
     * @see ConcurrentHashMap#putIfAbsent(Object, Object)
     */
    public static Object putIfAbsent(String key, Object value) {
        return value == null ? CONTEXT.remove(key) : CONTEXT.putIfAbsent(key, value);
    }

    /**
     * 设置上下文信息
     *
     * @param key   the key
     * @param value the value
     * @return the object
     * @see ConcurrentHashMap#put(Object, Object)
     */
    public static Object put(String key, Object value) {
        return value == null ? CONTEXT.remove(key) : CONTEXT.put(key, value);
    }

    /**
     * 得到全部上下文信息
     *
     * @return the CONTEXT
     */
    public static ConcurrentMap getContext() {
        return new ConcurrentHashMap(CONTEXT);
    }

}
