package com.enonic.cms.core.boot;

import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

public final class ConfigProperties
    extends Properties
{
    public Map<String, String> getMap()
    {
        return Maps.fromProperties( this );
    }
}
