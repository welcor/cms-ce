package com.enonic.cms.core.search.builder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import static junit.framework.Assert.assertEquals;

public class ContentIndexDateSetBuilderTest
{
    SimpleDateFormat formatter = new SimpleDateFormat( "EEE, dd-MMM-yyyy HH:mm:ss" );

    @Test
    public void testValidDateFormats()
    {
        Set<String> values = Sets.newTreeSet( Arrays.asList( "01.08.1975", "1975-08-02", "03.08.1975 00:10:30", "04.08.1975 00:10" ) );
        final Set<Date> translatedValues = ContentIndexDateSetBuilder.translateIndexValueSetToDates( "test", values );

        //printDates( translatedValues );
        assertEquals( 4, translatedValues.size() );
    }

    @Test
    public void testInvalidDateFormats()
    {
        Set<String> values = Sets.newTreeSet( Arrays.asList( "01 08 1975", "1/8/75" ) );
        final Set<Date> translatedValues = ContentIndexDateSetBuilder.translateIndexValueSetToDates( "test", values );

        //printDates( translatedValues );
        assertEquals( 0, translatedValues.size() );
    }

    @Test
    public void testStrangeButValidDates()
    {
        Set<String> values = Sets.newTreeSet( Arrays.asList( "1975.01.08", "1975-13-33", "40.40.40" ) );
        final Set<Date> translatedValues = ContentIndexDateSetBuilder.translateIndexValueSetToDates( "test", values );

        //printDates( translatedValues );
        assertEquals( 3, translatedValues.size() );
    }

    private void printDates( final Set<Date> translatedValues )
    {
        for ( Date date : translatedValues )
        {
            System.out.println( formatter.format( date ) );
        }
    }
}
