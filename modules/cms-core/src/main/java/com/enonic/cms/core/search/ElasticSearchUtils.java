package com.enonic.cms.core.search;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.CmsDateAndTimeFormats;

public class ElasticSearchUtils
{

    // 2012-02-09 11:43
    public final static SimpleDateFormat elasticsearchDateAsStringFormat =
        new SimpleDateFormat( CmsDateAndTimeFormats.XML_DATE_FORMAT_PATTERN );

    public static String formatDateAsStringIgnoreTimezone( final Date date )
    {
        return elasticsearchDateAsStringFormat.format( date );
    }

    public static String formatDateAsStringIgnoreTimezone( final ReadableDateTime date )
    {
        return elasticsearchDateAsStringFormat.format( date.toDateTime().toDate() );
    }

}
