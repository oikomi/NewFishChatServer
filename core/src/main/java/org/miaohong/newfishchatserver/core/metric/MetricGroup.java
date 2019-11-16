package org.miaohong.newfishchatserver.core.metric;

public interface MetricGroup {

    Counter counter(String name);

    <C extends Counter> C counter(String name, C counter);

}
