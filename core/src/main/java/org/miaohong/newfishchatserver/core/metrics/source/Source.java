package org.miaohong.newfishchatserver.core.metrics.source;

import com.codahale.metrics.MetricRegistry;

public interface Source {

    String getName();

    MetricRegistry register();
}
