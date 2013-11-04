/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public final class Version
{
    private final static Version INSTANCE = new Version();

    private final String nowTimestamp;

    private final Properties props;

    private Version()
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd.HHmmss" );
        this.nowTimestamp = dateFormat.format( new Date() );

        try
        {
            this.props = new Properties();
            final InputStream in = getClass().getResourceAsStream( "version.properties" );
            this.props.load( new InputStreamReader( in, "UTF8" ) );
        }
        catch ( final Exception e )
        {
            throw new Error( "Failed to load version.properties", e );
        }
    }

    private String getVersionProp()
    {
        final String value = this.props.getProperty( "version", "x.x.x" );
        if ( value.equalsIgnoreCase( "${project.version}" ) )
        {
            return "x.x.x";
        }
        else
        {
            return value;
        }
    }

    private String getTimestampProp()
    {
        final String value = this.props.getProperty( "timestamp", this.nowTimestamp );
        if ( value.equalsIgnoreCase( "${buildTimestamp}" ) )
        {
            return this.nowTimestamp;
        }
        else
        {
            return value;
        }
    }

    public static String getTitle()
    {
        return "Enonic CMS";
    }

    public static String getCopyright()
    {
        return "Copyright (c) 2000-2012 Enonic AS";
    }

    public static String getTimestamp()
    {
        return INSTANCE.getTimestampProp();
    }

    public static String getVersion()
    {
        final String version = INSTANCE.getVersionProp();
        if ( version.endsWith( "-SNAPSHOT" ) )
        {
            return version.replace( "-SNAPSHOT", "-" + getTimestamp() );
        }

        return version;
    }

    public static void main( String[] args )
    {
        System.out.println( getTitle() + " " + getVersion() );
    }
}
