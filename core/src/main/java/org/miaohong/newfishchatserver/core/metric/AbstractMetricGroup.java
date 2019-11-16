package org.miaohong.newfishchatserver.core.metric;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMetricGroup implements MetricGroup {

    private final Map<String, Metric> metrics = new HashMap<>();

    private MetricRegistry register;

    protected AbstractMetricGroup(MetricRegistry register) {

    }

    @Override
    public Counter counter(String name) {
        return counter(name, new SimpleCounter());
    }

    @Override
    public <C extends Counter> C counter(String name, C counter) {
        addMetric(name, counter);
        return counter;
    }

    protected void addMetric(String name, Metric metric) {
        metrics.put(name, metric);
        register.register(metric, name, this);
    }
}
