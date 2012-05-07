package com.enonic.cms.core.search;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

public class ElasticSearchUtilsTest
{
    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd't'HH:mm:ss:SSS Z" );

    @Test
    public void testDateStuff()
    {
        Date now = Calendar.getInstance().getTime();

        final String esDateString = ElasticSearchUtils.formatDateForElasticSearch( new DateTime( now ) );

        System.out.println( esDateString );

    }


}
