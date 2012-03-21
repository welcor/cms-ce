package com.enonic.cms.core.boot;

import org.springframework.core.env.PropertiesPropertySource;

final class ConfigPropertySource
    extends PropertiesPropertySource
{
    public ConfigPropertySource(final ConfigProperties props)
    {
        super("config", props);
    }
}
