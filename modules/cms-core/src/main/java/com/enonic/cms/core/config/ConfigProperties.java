package com.enonic.cms.core.config;

import java.util.Map;
import java.util.Properties;

public interface ConfigProperties
{
    public Properties getProperties();

    public Map<String, String> getMap();

    public <T> T getValue(final String key, final Class<T> type);

    public <T> T getValue(final String key, final Class<T> type, final T defValue);
}
