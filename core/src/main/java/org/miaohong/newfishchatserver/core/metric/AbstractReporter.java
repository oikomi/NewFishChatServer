package org.miaohong.newfishchatserver.core.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractReporter implements MetricReporter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReporter.class);

    protected final Map<Counter, String> counters = new HashMap<>();

    public AbstractReporter() {
    }

    @Override
    public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
        synchronized (this) {
            if (metric instanceof Counter) {
                counters.put((Counter) metric, metricName);
            } else {
                LOG.warn("Cannot add unknown metric type {}. This indicates that the reporter " +
                        "does not support this metric type.", metric.getClass().getName());
            }
        }
    }

    @Override
    public void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group) {
        synchronized (this) {
            if (metric instanceof Counter) {
                counters.remove(metric);
            } else {
                LOG.warn("Cannot remove unknown metric type {}. This indicates that the reporter " +
                        "does not support this metric type.", metric.getClass().getName());
            }
        }
    }

}
