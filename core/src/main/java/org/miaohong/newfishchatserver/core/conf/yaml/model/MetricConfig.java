package org.miaohong.newfishchatserver.core.conf.yaml.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MetricConfig implements ModelConfig {

//    private MetricConfig() {
//        throw new AssertionError();
//    }

    @Getter
    @Setter
    private List<String> sources;

    @Getter
    @Setter
    private List<String> sinks;
}
