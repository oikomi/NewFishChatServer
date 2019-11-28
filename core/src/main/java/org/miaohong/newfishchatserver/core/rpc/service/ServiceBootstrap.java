package org.miaohong.newfishchatserver.core.rpc.service;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import org.miaohong.newfishchatserver.core.rpc.eventbus.event.ServerStartedEvent;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;
import org.miaohong.newfishchatserver.core.rpc.server.config.ServerConfig;
import org.miaohong.newfishchatserver.core.rpc.service.config.ServiceConfig;
import org.miaohong.newfishchatserver.core.util.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceBootstrap<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceBootstrap.class);
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition connected = lock.newCondition();
    private final ExecutorService executorService = ThreadPoolUtils.newFixedThreadPool(1);
    private Register register;
    private ServiceConfig<T> serviceConfig;

    public ServiceBootstrap(Register register, ServiceConfig<T> serviceConfig) {
        this.register = register;
        this.serviceConfig = serviceConfig;
    }

    private static void signalAvailable() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void waitingForServerStarted() throws InterruptedException {
        lock.lock();
        try {
            connected.await();
        } finally {
            lock.unlock();
        }
    }

    private void initCheck() {
        Preconditions.checkNotNull(register);
        Preconditions.checkNotNull(serviceConfig);
    }

    public void export() {
        LOG.info("do service export");
        initCheck();
        executorService.submit(() -> {
            try {
                waitingForServerStarted();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

            serviceConfig.setServerConfig(ServiceBootstrapListener.serverConfig);
            LOG.info("start to export service");
            Preconditions.checkState(serviceConfig.getServerConfig() != null,
                    "server config can not empty");

            register.start();
            register.register(serviceConfig);
        });
    }

    public void unExport() {
    }

    public static class ServiceBootstrapListener {

        private static ServerConfig serverConfig;

        @Subscribe
        public static void doAction(final Object event) {
            LOG.info("Received event [{}] and will take a action", event);
            if (event instanceof ServerStartedEvent) {
                LOG.info("server is started");
                ServerStartedEvent serverStartedEvent = (ServerStartedEvent) event;
                serverConfig = serverStartedEvent.getServerConfig();
                signalAvailable();
            }
        }
    }
}
