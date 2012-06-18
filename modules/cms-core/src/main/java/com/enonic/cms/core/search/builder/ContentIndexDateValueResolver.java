package com.enonic.cms.core.search.builder;

import java.util.Date;

import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.index.util.ValueConverter;

public class ContentIndexDateValueResolver
{
    public static Date resolveDateValue( Object value )
    {
        if ( value == null )
        {
            return null;
        }

        ReadableDateTime date = ValueConverter.toDate( value.toString() );

        if ( date == null )
        {
            return null;
        }

        return date.toDateTime().toDate();
    }
}
