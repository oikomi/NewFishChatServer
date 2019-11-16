package org.miaohong.newfishchatserver.core.metric;

public interface MetricRegistry {

    void register(Metric metric, String metricName, MetricGroup group);
}
