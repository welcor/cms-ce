package com.enonic.cms.core.boot;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public final class ConfigProperties
    extends Properties
{

    public Map<String, String> getMap()
    {
        return Maps.fromProperties( this );
    }

    public Map<String, String> getPropertiesStartingWith( final String prefix )
    {
        return Maps.filterKeys( Maps.fromProperties( this ), new Predicate<String>()
        {
            @Override
            public boolean apply( @Nullable final String input )
            {
                return input != null && StringUtils.startsWith( input, prefix );
            }
        } );
    }

}
