package org.miaohong.newfishchatserver.core.conf;


import org.miaohong.newfishchatserver.core.util.PropUtil;

import java.util.Properties;

public abstract class BaseConfig implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Properties properties;

    public BaseConfig() {
        this.properties = PropUtil.loadProperties(getPropertiesPath());
    }

    public String getString(String propName) {
        return properties.getProperty(propName);
    }

    public int getInt(String propName, int defaultValue) {
        return PropUtil.getIntValue(propName, defaultValue, properties);
    }

    public long getLong(String propName, long defaultValue) {
        return PropUtil.getLongValue(propName, defaultValue, properties);
    }

    protected abstract String getPropertiesPath();

}
