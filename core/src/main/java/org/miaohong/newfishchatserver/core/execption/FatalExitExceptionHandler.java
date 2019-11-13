package org.miaohong.newfishchatserver.core.execption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FatalExitExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static final FatalExitExceptionHandler INSTANCE = new FatalExitExceptionHandler();
    private static final Logger LOG = LoggerFactory.getLogger(FatalExitExceptionHandler.class);

    @Override
    @SuppressWarnings("finally")
    public void uncaughtException(Thread t, Throwable e) {
        try {
            LOG.error("FATAL: Thread '" + t.getName() +
                    "' produced an uncaught exception. Stopping the process...", e);
        } finally {
            System.exit(-17);
        }
    }
}
