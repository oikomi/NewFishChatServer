package org.miaohong.newfishchatserver.core.rpc.concurrency;

import com.google.common.base.Preconditions;
import org.miaohong.newfishchatserver.core.execption.FatalExitExceptionHandler;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private static final String DEFAULT_POOL_NAME = "default-pool";

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final ThreadGroup group;

    private final String namePrefix;

    private final int threadPriority;

    @Nullable
    private final Thread.UncaughtExceptionHandler exceptionHandler;

    // ------------------------------------------------------------------------

    /**
     * Creates a new thread factory using the default thread pool name ('flink-executor-pool')
     * and the default uncaught exception handler (log exception and kill process).
     */
    public NamedThreadFactory() {
        this(DEFAULT_POOL_NAME);
    }

    /**
     * Creates a new thread factory using the given thread pool name and the default
     * uncaught exception handler (log exception and kill process).
     *
     * @param poolName The pool name, used as the threads' name prefix
     */
    public NamedThreadFactory(String poolName) {
        this(poolName, FatalExitExceptionHandler.INSTANCE);
    }

    /**
     * Creates a new thread factory using the given thread pool name and the given
     * uncaught exception handler.
     *
     * @param poolName         The pool name, used as the threads' name prefix
     * @param exceptionHandler The uncaught exception handler for the threads
     */
    public NamedThreadFactory(String poolName, Thread.UncaughtExceptionHandler exceptionHandler) {
        this(poolName, Thread.NORM_PRIORITY, exceptionHandler);
    }

    NamedThreadFactory(
            final String poolName,
            final int threadPriority,
            @Nullable final Thread.UncaughtExceptionHandler exceptionHandler) {
        this.namePrefix = Preconditions.checkNotNull(poolName, "poolName") + "-thread-";
        this.threadPriority = threadPriority;
        this.exceptionHandler = exceptionHandler;

        SecurityManager securityManager = System.getSecurityManager();
        this.group = (securityManager != null) ? securityManager.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
    }

    // ------------------------------------------------------------------------

    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement());
        t.setDaemon(true);

        t.setPriority(threadPriority);

        // optional handler for uncaught exceptions
        if (exceptionHandler != null) {
            t.setUncaughtExceptionHandler(exceptionHandler);
        }

        return t;
    }

    // --------------------------------------------------------------------------------------------

    public static final class Builder {
        private String poolName;
        private int priority = Thread.NORM_PRIORITY;
        private Thread.UncaughtExceptionHandler exceptionHandler = FatalExitExceptionHandler.INSTANCE;

        public Builder setPoolName(final String poolName) {
            this.poolName = poolName;
            return this;
        }

        public Builder setThreadPriority(final int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setExceptionHandler(final Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public NamedThreadFactory build() {
            return new NamedThreadFactory(poolName, priority, exceptionHandler);
        }
    }
}
