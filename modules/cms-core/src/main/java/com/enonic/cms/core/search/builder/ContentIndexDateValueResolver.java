package com.enonic.cms.core.search.builder;

import java.util.Date;

import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.search.query.IndexValueConverter;

public class ContentIndexDateValueResolver
{
    public static Date resolveDateValue( Object value )
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

        return date.toDateTime().toDate();
    }
}
