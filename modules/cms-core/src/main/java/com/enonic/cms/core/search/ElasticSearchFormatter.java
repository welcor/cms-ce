package com.enonic.cms.core.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.CmsDateAndTimeFormats;

public class ElasticSearchFormatter
{

    public final static SimpleDateFormat elasticsearchSimpleDateFormat =
        new SimpleDateFormat( CmsDateAndTimeFormats.XML_DATE_FORMAT_PATTERN );

    public final static SimpleDateFormat elasticsearchFullDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" );

    public final static SimpleDateFormat elasticsearchDateOptionalTimeFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );

    public static String formatDateAsStringIgnoreTimezone( final Date date )
    {
        return elasticsearchSimpleDateFormat.format( date );
    }

    public static String formatDateAsStringIgnoreTimezone( final ReadableDateTime date )
    {
        return elasticsearchSimpleDateFormat.format( date.toDateTime().toDate() );
    }

    public static String formatDateAsStringFull( final Date date )
    {
        return elasticsearchFullDateFormat.format( date );
    }

    public static String formatDateAsStringFullWithTimezone( final ReadableDateTime dateTime )
    {
        return elasticsearchDateOptionalTimeFormat.format( dateTime.toDateTime().toDate() );
    }

    public static ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }

    public static Date parseStringAsElasticsearchDateOptionalTimeFormat( final String dateString )
    {
        try
        {
            return elasticsearchDateOptionalTimeFormat.parse( dateString );
        }
        catch ( ParseException e )
        {
            return null;
        }
    }


}
