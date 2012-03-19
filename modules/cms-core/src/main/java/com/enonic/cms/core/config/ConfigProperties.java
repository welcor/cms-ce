package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public interface ConfigProperties
{
    public Properties getProperties();

    public Map<String, String> getMap();

    public <T> T getValue(final String key, final Class<T> type);

    public <T> T getValue(final String key, final Class<T> type, final T defValue);

    public String getString(final String key);

    public String getString(final String key, final String defValue);

    public Boolean getBoolean(final String key);
    
    public Boolean getBoolean(final String key, final Boolean defValue);

    public Integer getInteger(final String key);

    public Integer getInteger(final String key, final Integer defValue);

    public Long getLong(final String key);

    public Long getLong(final String key, final Long defValue);

    public File getFile(final String key);

    public File getFile(final String key, final File defValue);
}
