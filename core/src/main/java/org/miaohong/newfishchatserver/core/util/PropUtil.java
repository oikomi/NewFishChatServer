package org.miaohong.newfishchatserver.core.util;


import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PropUtil.class);

    private PropUtil() {

    }

    public static Properties loadProperties(String path) {
        LOG.info("load properties from file: {}", path);
        Properties properties = new Properties();
        try {
            InputStream in = PropUtil.class.getClassLoader().getResourceAsStream(path);
            properties.load(in);
        } catch (Exception ex) {
            LOG.error("can not read properties from file: {}, msg: {}", path, ex.getMessage());
        }
        return properties;
    }


    public static InputStream loadUrl(String path) {
        return PropUtil.class.getClassLoader().getResourceAsStream(path);
    }

    public static boolean propertiesExist(String path) {
        URL url = PropUtil.class.getClassLoader().getResource(path);
        return null != url;
    }

    public static int getIntValue(String key, int defaultValue, Properties props) {
        if (!props.containsKey(key)) {
            LOG.info("not setting {} value, used default value: {}", key, defaultValue);
            return defaultValue;
        }

        String valueString = (String) props.get(key);
        int value = defaultValue;
        if (!Strings.isNullOrEmpty(valueString)) {
            try {
                value = Integer.parseInt(valueString);
            } catch (Exception ex) {
                LOG.error("failed to convert {} value: {}, used default value: {}",
                        key, valueString, defaultValue);
            }
        }
        return value;
    }

    public static String getStringValue(String key, Properties props) {
        return (String) props.get(key);
    }

    public static long getLongValue(String key, long defaultValue, Properties props) {
        if (!props.containsKey(key)) {
            LOG.info("not setting {} value, used default value: {}", key, defaultValue);
            return defaultValue;
        }

        String valueString = (String) props.get(key);
        long value = defaultValue;
        if (!Strings.isNullOrEmpty(valueString)) {
            try {
                value = Long.parseLong(valueString);
            } catch (Exception ex) {
                LOG.error("failed to convert {} value: {}, used default value: {}",
                        key, valueString, defaultValue);
            }
        }
        return value;
    }
}
