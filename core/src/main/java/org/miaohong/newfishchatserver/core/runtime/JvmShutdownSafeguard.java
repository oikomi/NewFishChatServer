package org.miaohong.newfishchatserver.core.runtime;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;

public class JvmShutdownSafeguard extends Thread {

    /**
     * Default delay to wait after clean shutdown was stared, before forcibly terminating the JVM
     */
    private static final long DEFAULT_DELAY = 5000L;

    /**
     * The exit code returned by the JVM process if it is killed by the safeguard
     */
    private static final int EXIT_CODE = -17;

    /**
     * The thread that actually does the termination
     */
    private final Thread terminator;

    private JvmShutdownSafeguard(long delayMillis) {
        setName("JVM Terminator Launcher");

        this.terminator = new Thread(new DelayedTerminator(delayMillis), "Jvm Terminator");
        this.terminator.setDaemon(true);
    }

    /**
     * Installs the safeguard shutdown hook. The maximum time that the JVM is allowed to spend
     * on shutdown before being killed is five seconds.
     *
     * @param logger The logger to log errors to.
     */
    public static void installAsShutdownHook(Logger logger) {
        installAsShutdownHook(logger, DEFAULT_DELAY);
    }

    // ------------------------------------------------------------------------
    //  The actual Shutdown thread
    // ------------------------------------------------------------------------

    /**
     * Installs the safeguard shutdown hook. The maximum time that the JVM is allowed to spend
     * on shutdown before being killed is the given number of milliseconds.
     *
     * @param logger      The logger to log errors to.
     * @param delayMillis The delay (in milliseconds) to wait after clean shutdown was stared,
     *                    before forcibly terminating the JVM.
     */
    public static void installAsShutdownHook(Logger logger, long delayMillis) {
        Preconditions.checkArgument(delayMillis >= 0, "delay must be >= 0");

        // install the blocking shutdown hook
        Thread shutdownHook = new JvmShutdownSafeguard(delayMillis);
        ShutdownHookUtil.addShutdownHookThread(shutdownHook, JvmShutdownSafeguard.class.getSimpleName(), logger);
    }

    // ------------------------------------------------------------------------
    //  Installing as a shutdown hook
    // ------------------------------------------------------------------------

    @Override
    public void run() {
        // Because this thread is registered as a shutdown hook, we cannot
        // wait here and then call for termination. That would always delay the JVM shutdown.
        // Instead, we spawn a non shutdown hook thread from here.
        // That thread is a daemon, so it does not keep the JVM alive.
        terminator.start();
    }

    private static class DelayedTerminator implements Runnable {

        private final long delayMillis;

        private DelayedTerminator(long delayMillis) {
            this.delayMillis = delayMillis;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delayMillis);
            } catch (Throwable t) {
                // catch all, including thread death, etc
            }

            Runtime.getRuntime().halt(EXIT_CODE);
        }
    }
}
