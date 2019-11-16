package org.miaohong.newfishchatserver.core.metric;

public interface MetricReporter {

    void open();

    void close();

    void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group);

    void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group);

}
