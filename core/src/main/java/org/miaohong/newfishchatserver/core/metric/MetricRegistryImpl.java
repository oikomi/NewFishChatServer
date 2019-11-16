package org.miaohong.newfishchatserver.core.metric;

import org.miaohong.newfishchatserver.core.extension.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricRegistryImpl implements MetricRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(MetricRegistryImpl.class);

    private final List<MetricReporter> reporters;
    private final ScheduledExecutorService executor;

    public MetricRegistryImpl() {
        this.reporters = ExtensionLoader.getExtensionLoader(MetricReporter.class).getAllExtension(MetricReporter.class);
        this.executor = Executors.newSingleThreadScheduledExecutor();

        for (MetricReporter reporterInstance : reporters) {
            TimeUnit timeunit = TimeUnit.SECONDS;
            long period = 10;

            if (reporterInstance instanceof Scheduled) {
                executor.scheduleWithFixedDelay(
                        new MetricRegistryImpl.ReporterTask((Scheduled) reporterInstance), period, period, timeunit);
            }
        }
    }

    @Override
    public void register(Metric metric, String metricName, MetricGroup group) {
        for (MetricReporter reporter : reporters) {
            reporter.notifyOfAddedMetric(metric, metricName, group);
        }
    }


    private static final class ReporterTask extends TimerTask {

        private final Scheduled reporter;

        private ReporterTask(Scheduled reporter) {
            this.reporter = reporter;
        }

        @Override
        public void run() {
            try {
                reporter.report();
            } catch (Throwable t) {
                LOG.warn("Error while reporting metrics", t);
            }
        }
    }

}
