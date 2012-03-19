package com.enonic.cms.core.config;

import java.util.Map;
import java.util.Properties;

import org.springframework.core.convert.ConversionService;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

final class ConfigPropertiesImpl
    extends Properties implements ConfigProperties
{
    private final ConversionService converter;

    public ConfigPropertiesImpl( final ConversionService converter )
    {
        this.converter = converter;
    }

    public Map<String, String> getMap()
    {
        return Maps.fromProperties(this);
    }

    public <T> T getValue(final String key, final Class<T> type)
    {
        final T value = getValue( key, type, null );
        if (value == null) {
            throw new IllegalArgumentException("No value for configuration property [" + key + "]");
        }

        return value;
    }

    public <T> T getValue(final String key, final Class<T> type, final T defValue)
    {
        final String value = getProperty(key);
        if (Strings.isNullOrEmpty(value)) {
            return defValue;
        }

        return this.converter.convert(value, type);
    }

    public Properties getProperties()
    {
        final Properties target = new Properties();
        target.putAll(this);
        return target;
    }
}
