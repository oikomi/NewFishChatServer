package org.miaohong.newfishchatserver.core.conf;


import org.miaohong.newfishchatserver.core.util.PropUtils;

import java.util.Properties;

public abstract class BaseConfig implements Config, java.io.Serializable {

    private static final long serialVersionUID = 5429008745932219543L;

    private Properties properties;

    public BaseConfig() {
        this.properties = PropUtils.loadProperties(getPropertiesPath());
    }

    @Override
    public String getString(String propName) {
        return properties.getProperty(propName);
    }

    @Override
    public int getInt(String propName, int defaultValue) {
        return PropUtils.getIntValue(propName, defaultValue, properties);
    }

    @Override
    public long getLong(String propName, long defaultValue) {
        return PropUtils.getLongValue(propName, defaultValue, properties);
    }

    @Override
    public boolean getBoolean(String propName, Boolean defaultValue) {
        return PropUtils.getBooleanValue(propName, defaultValue, properties);
    }

    protected abstract String getPropertiesPath();

}
