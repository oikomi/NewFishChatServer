package org.miaohong.newfishchatserver.core.metrics.sink;

public interface Sink {
    void start();

    void stop();

    void report();
}
