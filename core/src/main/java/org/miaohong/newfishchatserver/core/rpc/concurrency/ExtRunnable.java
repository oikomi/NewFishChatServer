package org.miaohong.newfishchatserver.core.rpc.concurrency;

import org.miaohong.newfishchatserver.core.execption.UncheckedCheckedException;

public interface ExtRunnable<E extends Throwable> {
    static Runnable quiet(ExtRunnable<?> runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new UncheckedCheckedException(e);
            }
        };
    }

    void run() throws E;
}
