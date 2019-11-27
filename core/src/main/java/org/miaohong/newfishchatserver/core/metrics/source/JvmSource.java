package org.miaohong.newfishchatserver.core.metrics.source;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;

import java.lang.management.ManagementFactory;

public class JvmSource implements Source {

    private String sourceName = "jvm";

    @Override
    public String getName() {
        return sourceName;
    }

    @Override
    public MetricRegistry register() {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.registerAll(new GarbageCollectorMetricSet());
        metricRegistry.registerAll(new MemoryUsageGaugeSet());
        metricRegistry.registerAll(
                new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));

        return metricRegistry;
    }
}
