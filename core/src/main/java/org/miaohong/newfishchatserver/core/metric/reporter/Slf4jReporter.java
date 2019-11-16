package org.miaohong.newfishchatserver.core.metric.reporter;

import org.miaohong.newfishchatserver.annotations.SpiMeta;
import org.miaohong.newfishchatserver.core.metric.AbstractReporter;
import org.miaohong.newfishchatserver.core.metric.Counter;
import org.miaohong.newfishchatserver.core.metric.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.Map;


@SpiMeta(name = "slf4j")
public class Slf4jReporter extends AbstractReporter implements Scheduled {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jReporter.class);

    private static final String lineSeparator = System.lineSeparator();

    // the initial size roughly fits ~150 metrics with default scope settings
    private int previousSize = 16384;

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public void report() {
        try {
            tryReport();
        } catch (ConcurrentModificationException ignored) {
            // at tryReport() we don't synchronize while iterating over the various maps which might cause a
            // ConcurrentModificationException to be thrown, if concurrently a metric is being added or removed.
        }
    }

    private void tryReport() {
        // initialize with previous size to avoid repeated resizing of backing array
        // pad the size to allow deviations in the final string, for example due to different double value representations
        StringBuilder builder = new StringBuilder((int) (previousSize * 1.1));

        builder
                .append(lineSeparator)
                .append("=========================== Starting metrics report ===========================")
                .append(lineSeparator);

        builder
                .append(lineSeparator)
                .append("-- Counters -------------------------------------------------------------------")
                .append(lineSeparator);
        for (Map.Entry<Counter, String> metric : counters.entrySet()) {
            builder
                    .append(metric.getValue()).append(": ").append(metric.getKey().getCount())
                    .append(lineSeparator);
        }

        builder
                .append(lineSeparator)
                .append("=========================== Finished metrics report ===========================")
                .append(lineSeparator);
        LOG.info(builder.toString());

        previousSize = builder.length();
    }

}
