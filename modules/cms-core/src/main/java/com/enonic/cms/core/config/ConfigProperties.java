package com.enonic.cms.core.config;

import java.util.Map;
import java.util.Properties;

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

    public Map<String, String> getMapStartingWith( final String prefix )
    {
        return Maps.filterKeys( getMap(), new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, prefix );
            }
        } );
    }

    public String getJdbcLogging()
    {
        return getProperty( "cms.jdbc.logging" );
    }

}
