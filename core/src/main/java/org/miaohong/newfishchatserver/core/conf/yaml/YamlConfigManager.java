package org.miaohong.newfishchatserver.core.conf.yaml;

import com.google.common.collect.Maps;
import org.miaohong.newfishchatserver.core.conf.yaml.model.ModelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class YamlConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(YamlConfigManager.class);

    private static Map<String, ModelConfig> configs = Maps.newConcurrentMap();

    public static ModelConfig get(String path, Class<org.miaohong.newfishchatserver.core.conf.yaml.model.MetricConfig> clz) {
        if (configs.containsKey(path)) {
            return configs.get(path);
        }

        load(path, clz);

        return configs.get(path);
    }

    private static void load(String path, Class clz) {
        Config config = new YamlConfig<>(path, clz);
        configs.put(path, (ModelConfig) config.load());
    }

}
