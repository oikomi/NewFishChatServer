package org.miaohong.newfishchatserver.core.metrics.sink;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

public class ConsoleSink implements Sink {

    private ConsoleReporter reporter;

    public ConsoleSink(MetricRegistry registry) {
        this.reporter = ConsoleReporter.forRegistry(registry)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertRatesTo(TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void start() {
        reporter.start(10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        reporter.stop();
    }

    @Override
    public void report() {
        reporter.report();
    }
}
