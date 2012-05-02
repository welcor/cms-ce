package com.enonic.cms.core.search;

import org.joda.time.ReadableDateTime;
import org.joda.time.format.ISODateTimeFormat;

public class ElasticSearchUtils
{

    public static String formatDateForElasticSearch( final ReadableDateTime date )
    {
        return ISODateTimeFormat.dateTime().withZoneUTC().print( date ).toLowerCase();
    }

}
