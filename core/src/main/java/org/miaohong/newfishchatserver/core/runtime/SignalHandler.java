package org.miaohong.newfishchatserver.core.runtime;

import org.slf4j.Logger;
import sun.misc.Signal;

public class SignalHandler {

    private static boolean registered = false;

    /**
     * Our signal handler.
     */
    private static class Handler implements sun.misc.SignalHandler {

        private final Logger LOG;
        private final sun.misc.SignalHandler prevHandler;

        Handler(String name, Logger LOG) {
            this.LOG = LOG;
            prevHandler = Signal.handle(new Signal(name), this);
        }

        /**
         * Handle an incoming signal.
         *
         * @param signal    The incoming signal
         */
        @Override
        public void handle(Signal signal) {
            LOG.info("RECEIVED SIGNAL {}: SIG{}. Shutting down as requested.",
                    signal.getNumber(),
                    signal.getName());
            prevHandler.handle(signal);
        }
    }

    /**
     * Register some signal handlers.
     *
     * @param LOG The slf4j logger
     */
    public static void register(final Logger LOG) {
        synchronized (SignalHandler.class) {
            if (registered) {
                return;
            }
            registered = true;

            final String[] SIGNALS = OperatingSystem.isWindows()
                    ? new String[]{ "TERM", "INT"}
                    : new String[]{ "TERM", "HUP", "INT" };

            StringBuilder bld = new StringBuilder();
            bld.append("Registered UNIX signal handlers for [");

            String separator = "";
            for (String signalName : SIGNALS) {
                try {
                    new Handler(signalName, LOG);
                    bld.append(separator);
                    bld.append(signalName);
                    separator = ", ";
                } catch (Exception e) {
                    LOG.info("Error while registering signal handler", e);
                }
            }
            bld.append("]");
            LOG.info(bld.toString());
        }
    }

}
