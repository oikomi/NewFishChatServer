package org.miaohong.newfishchatserver.core.runtime;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;

public class ShutdownHookUtil {

    private ShutdownHookUtil() {
        throw new AssertionError();
    }

    /**
     * Adds a shutdown hook to the JVM and returns the Thread, which has been registered.
     */
    public static Thread addShutdownHook(
            final AutoCloseable service,
            final String serviceName,
            final Logger logger) {

        Preconditions.checkNotNull(service);
        Preconditions.checkNotNull(logger);

        final Thread shutdownHook = new Thread(() -> {
            try {
                service.close();
            } catch (Throwable t) {
                logger.error("Error during shutdown of {} via JVM shutdown hook.", serviceName, t);
            }
        }, serviceName + " shutdown hook");

        return addShutdownHookThread(shutdownHook, serviceName, logger) ? shutdownHook : null;
    }

    /**
     * Adds a shutdown hook to the JVM.
     *
     * @param shutdownHook Shutdown hook to be registered.
     * @param serviceName  The name of service.
     * @param logger       The logger to log.
     * @return Whether the hook has been successfully registered.
     */
    public static boolean addShutdownHookThread(
            final Thread shutdownHook,
            final String serviceName,
            final Logger logger) {

        Preconditions.checkNotNull(shutdownHook);
        Preconditions.checkNotNull(logger);

        try {
            // Add JVM shutdown hook to call shutdown of service
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            return true;
        } catch (IllegalStateException e) {
            // JVM is already shutting down. no need to do our work
        } catch (Throwable t) {
            logger.error("Cannot register shutdown hook that cleanly terminates {}.", serviceName, t);
        }
        return false;
    }

    /**
     * Removes a shutdown hook from the JVM.
     */
    public static void removeShutdownHook(final Thread shutdownHook, final String serviceName, final Logger logger) {

        // Do not run if this is invoked by the shutdown hook itself
        if (shutdownHook == null || shutdownHook == Thread.currentThread()) {
            return;
        }

        Preconditions.checkNotNull(logger);

        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException e) {
            // race, JVM is in shutdown already, we can safely ignore this
            logger.debug("Unable to remove shutdown hook for {}, shutdown already in progress", serviceName, e);
        } catch (Throwable t) {
            logger.warn("Exception while un-registering {}'s shutdown hook.", serviceName, t);
        }
    }

}
