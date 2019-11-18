package org.miaohong.newfishchatserver.core.conf;


public interface Config {
    String getString(String propName);

    int getInt(String propName, int defaultValue);

    long getLong(String propName, long defaultValue);

    boolean getBoolean(String propName, Boolean defaultValue);

}