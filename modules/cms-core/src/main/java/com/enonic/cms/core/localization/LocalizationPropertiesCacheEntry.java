package com.enonic.cms.core.localization;

import java.util.Properties;

import com.enonic.cms.core.resource.ResourceKey;

final class LocalizationPropertiesCacheEntry
{
    private final ResourceKey key;

    private final Properties properties;

    private final long timestamp;

    private long lastValidated;

    public LocalizationPropertiesCacheEntry( final ResourceKey key, final Properties properties )
    {
        this.key = key;
        this.properties = properties;
        this.timestamp = System.currentTimeMillis();
        this.lastValidated = this.timestamp;
    }

    public ResourceKey getKey()
    {
        return this.key;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public Properties getProperties()
    {
        return this.properties;
    }

    public long getLastValidated()
    {
        return lastValidated;
    }

    public void setLastValidated( final long lastValidated )
    {
        this.lastValidated = lastValidated;
    }
}
