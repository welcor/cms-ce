package com.enonic.cms.core.search;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElasticSearchFormatterTest
{
    @Test
    public void testDateFormatting()
    {
        DateTime dateTime = new DateTime( 2010, 8, 1, 12, 0, 30, 333 );

        final String esDateString = ElasticSearchFormatter.formatDateAsStringIgnoreTimezone( dateTime.toDate() );

        assertEquals( "2010-08-01 12:00", esDateString );

    }
}
