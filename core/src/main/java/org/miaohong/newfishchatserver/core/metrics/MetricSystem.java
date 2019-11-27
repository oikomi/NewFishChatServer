package org.miaohong.newfishchatserver.core.metrics;

import com.codahale.metrics.MetricRegistry;
import org.miaohong.newfishchatserver.core.conf.yaml.YamlConfigManager;
import org.miaohong.newfishchatserver.core.conf.yaml.model.MetricConfig;
import org.miaohong.newfishchatserver.core.metrics.sink.Sink;
import org.miaohong.newfishchatserver.core.metrics.source.Source;
import org.miaohong.newfishchatserver.core.util.ClassUtils;
import org.miaohong.newfishchatserver.core.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MetricSystem {

    private static final Logger LOG = LoggerFactory.getLogger(MetricSystem.class);

    private static final String METRIC_CONF_PATH = "config/metric.yaml";

    private MetricConfig metricConfig;

    private MetricRegistry registry = new MetricRegistry();

    public MetricSystem() {
        this.metricConfig
                = (MetricConfig) YamlConfigManager.get(METRIC_CONF_PATH, MetricConfig.class);
    }


    public void start() {
        LOG.info("start metric system");
        registerSources();
        registerSinks();
    }

    private void registerSources() {
        List<String> sources = metricConfig.getSources();
        if (CommonUtils.isNotEmpty(sources)) {
            sources.forEach(this::registerSource);
        }
    }

    private void registerSource(String clazz) {
        Object o = null;
        try {
            o = ClassUtils.forName(clazz).getConstructor().newInstance();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        if (o instanceof Source) {
            Source source = (Source) o;
            registry.register(source.getName(), source.register());
        }
    }

    private void registerSinks() {
        List<String> sinks = metricConfig.getSinks();
        if (CommonUtils.isNotEmpty(sinks)) {
            sinks.forEach(this::registerSink);
        }
    }

    private void registerSink(String clazz) {
        Object o = null;
        try {
            o = ClassUtils.forName(clazz).getConstructor(MetricRegistry.class).newInstance(registry);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        if (o instanceof Sink) {
            ((Sink) o).start();
        }
    }
}
