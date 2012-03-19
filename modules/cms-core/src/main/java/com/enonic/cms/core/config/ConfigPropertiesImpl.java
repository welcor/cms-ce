package com.enonic.cms.core.config;

import java.io.File;
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

    public String getString( final String key )
    {
        return getValue( key, String.class );
    }

    public String getString( final String key, final String defValue )
    {
        return getValue( key, String.class, defValue );
    }

    public Boolean getBoolean( final String key )
    {
        return getValue( key, Boolean.class );
    }

    public Boolean getBoolean( final String key, final Boolean defValue )
    {
        return getValue( key, Boolean.class, defValue );
    }

    public Integer getInteger( final String key )
    {
        return getValue( key, Integer.class );
    }

    public Integer getInteger( final String key, final Integer defValue )
    {
        return getValue( key, Integer.class, defValue );
    }

    public Long getLong( final String key )
    {
        return getValue( key, Long.class );
    }

    public Long getLong( final String key, final Long defValue )
    {
        return getValue( key, Long.class, defValue );
    }

    public File getFile( final String key )
    {
        return getValue( key, File.class );
    }

    public File getFile( final String key, final File defValue )
    {
        return getValue( key, File.class, defValue );
    }

    public Properties getProperties()
    {
        final Properties target = new Properties();
        target.putAll(this);
        return target;
    }
}
