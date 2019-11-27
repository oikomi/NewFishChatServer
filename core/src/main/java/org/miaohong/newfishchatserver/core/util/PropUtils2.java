package org.miaohong.newfishchatserver.core.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropUtils2 {

    private static final Logger LOG = LoggerFactory.getLogger(PropUtils2.class);

    private PropUtils2() {
        throw new AssertionError();
    }

    public static Configuration loadProperties(String path) {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(
                                params.properties().
                                        setFileName(path).
                                        setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            // loading of the configuration file failed
            LOG.error("can not read properties from file: {}, msg: {}", path, e.getMessage());
            throw new RuntimeException("can not read properties from file");
        }
    }


    public static String getStringValue(String key, Configuration config) {
        return config.getString(key);
    }

    public static int getIntValue(String key, int defaultValue, Configuration config) {
        if (!config.containsKey(key)) {
            LOG.info("not setting {} value, used default value: {}", key, defaultValue);
            return defaultValue;
        }

        return config.getInt(key);
    }

    public static long getLongValue(String key, long defaultValue, Configuration config) {
        if (!config.containsKey(key)) {
            LOG.info("not setting {} value, used default value: {}", key, defaultValue);
            return defaultValue;
        }

        return config.getLong(key);
    }

    public static boolean getBooleanValue(String key, Boolean defaultValue, Configuration config) {
        if (!config.containsKey(key)) {
            LOG.info("not setting {} value, used default value: {}", key, defaultValue);
            return defaultValue;
        }

        return config.getBoolean(key);
    }

}
