/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

import java.util.Date;

import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.search.query.IndexValueConverter;

public class ContentIndexDateValueResolver
{
    public static Date resolveDateValue( Object value )
    {
        ReadableDateTime date = doResolveDateValue( value );

        if ( date == null )
        {
            return null;
        }

        return date.toDateTime().toDate();
    }

    private static ReadableDateTime doResolveDateValue( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        ReadableDateTime date = IndexValueConverter.toDate( value.toString() );

        if ( date == null )
        {
            return null;
        }
        return date;
    }

    public static ReadableDateTime resolveReadableDateTimeValue( final Object value )
    {
        return doResolveDateValue( value );
    }

}
