package org.miaohong.newfishchatserver.core.conf.prop;


import org.apache.commons.configuration2.Configuration;
import org.miaohong.newfishchatserver.core.util.PropUtils2;

public abstract class BasePropConfig implements PropConfig, java.io.Serializable {

    private static final long serialVersionUID = 5429008745932219543L;

    private Configuration configuration;

    public BasePropConfig() {
        this.configuration = PropUtils2.loadProperties(getPropertiesPath());
    }

    @Override
    public String getString(String propName) {
        return PropUtils2.getStringValue(propName, configuration);
    }

    @Override
    public int getInt(String propName, int defaultValue) {
        return PropUtils2.getIntValue(propName, defaultValue, configuration);
    }

    @Override
    public long getLong(String propName, long defaultValue) {
        return PropUtils2.getLongValue(propName, defaultValue, configuration);
    }

    @Override
    public boolean getBoolean(String propName, Boolean defaultValue) {
        return PropUtils2.getBooleanValue(propName, defaultValue, configuration);
    }

    protected abstract String getPropertiesPath();

}
