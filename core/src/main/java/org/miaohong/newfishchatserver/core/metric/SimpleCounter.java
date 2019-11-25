package org.miaohong.newfishchatserver.core.metric;

import java.util.concurrent.atomic.AtomicLong;

public class SimpleCounter implements Counter {

    private AtomicLong count = new AtomicLong();

    @Override
    public void inc() {
        count.incrementAndGet();
    }

    @Override
    public void inc(long n) {
        count.addAndGet(n);
    }

    @Override
    public void dec() {
        count.decrementAndGet();
    }

    @Override
    public void dec(long n) {
        count.addAndGet(-n);
    }

    @Override
    public long getCount() {
        return count.get();
    }

}
