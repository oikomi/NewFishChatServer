package org.miaohong.newfishchatserver.core.conf;


public interface PropConfig {
    String getString(String propName);

    int getInt(String propName, int defaultValue);

    long getLong(String propName, long defaultValue);

    boolean getBoolean(String propName, Boolean defaultValue);

}
