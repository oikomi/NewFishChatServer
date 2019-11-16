package org.miaohong.newfishchatserver.core.metric;

public interface Counter extends Metric {

    void inc();

    void inc(long n);

    void dec();

    void dec(long n);

    long getCount();

}
