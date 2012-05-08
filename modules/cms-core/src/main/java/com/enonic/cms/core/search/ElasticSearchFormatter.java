package com.enonic.cms.core.search;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.CmsDateAndTimeFormats;

public class ElasticSearchFormatter
{

    public final static SimpleDateFormat elasticsearchSimpleDateFormat =
        new SimpleDateFormat( CmsDateAndTimeFormats.XML_DATE_FORMAT_PATTERN );

    public final static SimpleDateFormat elasticsearchFullDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

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


}
