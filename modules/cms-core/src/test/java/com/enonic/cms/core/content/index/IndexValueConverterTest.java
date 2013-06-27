/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Test;

import com.enonic.cms.core.search.query.IndexValueConverter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;


public class IndexValueConverterTest
{

    @Test
    public void testToTypedStringWithDate()
    {

        String orderBefore = IndexValueConverter.toTypedString( new GregorianCalendar( 2008, 6, 1, 12, 0, 0 ).getTime() );
        String orderAfter = IndexValueConverter.toTypedString( new GregorianCalendar( 2008, 6, 1, 12, 0, 1 ).getTime() );
        assertTrue( orderBefore.compareTo( orderAfter ) < 0 );

        String orderSame1 = IndexValueConverter.toTypedString( new GregorianCalendar( 2008, 7, 1, 12, 0, 0 ).getTime() );
        String orderSame2 = IndexValueConverter.toTypedString( new GregorianCalendar( 2008, 7, 1, 12, 0, 0 ).getTime() );
        assertTrue( orderSame1.compareTo( orderSame2 ) == 0 );
    }

    @Test
    public void testInvalidDateTimeConversionsFail()
    {
        doTestInvalidDateConversion( "2012/02/4" );

        doTestInvalidDateConversion( "2012.02.4" );

        doTestInvalidDateConversion( "2012.12.12" );

        doTestInvalidDateConversion( "2012-13-01" );

        doTestInvalidDateConversion( "2012-00-01" );

        doTestInvalidDateConversion( "2013-02-29" );

        doTestInvalidDateConversion( "2012 02 14" );
    }

    private void doTestInvalidDateConversion( final String dateValue )
    {
        final ReadableDateTime date = IndexValueConverter.toDate( dateValue );
        assertNull( "Incorrect conversion of invalid date: " + dateValue + " , parsed as " + date, date );
    }

    @Test
    public void testDateTimeConversions()
    {

        final ReadableDateTime date = IndexValueConverter.toDate( "2012-02-14" );
        final DateTime dateExpected = new DateTime( 2012, 2, 14, 0, 0, DateTimeZone.getDefault() );
        assertDateTimeEquals( date, dateExpected );

        final ReadableDateTime dateSingleDigit = IndexValueConverter.toDate( "2012-2-5" );
        final DateTime dateSingleDigitExpected = new DateTime( 2012, 2, 5, 0, 0, DateTimeZone.getDefault() );
        assertDateTimeEquals( dateSingleDigit, dateSingleDigitExpected );

        final ReadableDateTime dateTimeSeconds = IndexValueConverter.toDate( "2012-02-14 13:35:57" );
        final DateTime dateTimeSecondsExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.getDefault() );
        assertDateTimeEquals( dateTimeSeconds, dateTimeSecondsExpected );

        final ReadableDateTime dateTimeMinutes = IndexValueConverter.toDate( "2012-02-14 13:35" );
        final DateTime dateTimeMinutesExpected = new DateTime( 2012, 2, 14, 13, 35, 0, DateTimeZone.getDefault() );
        assertDateTimeEquals( dateTimeMinutes, dateTimeMinutesExpected );

        final ReadableDateTime dateTimeSecondsSep = IndexValueConverter.toDate( "2012-02-14T13:35:57" );
        final DateTime dateTimeSecondsSepExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.getDefault() );
        assertDateTimeEquals( dateTimeSecondsSep, dateTimeSecondsSepExpected );

        final ReadableDateTime dateTimeMilliseconds = IndexValueConverter.toDate( "2012-02-14T13:35:57.000" );
        final DateTime dateTimeMillisecondsExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.getDefault() );
        assertDateTimeEquals( dateTimeMilliseconds, dateTimeMillisecondsExpected );

        final ReadableDateTime dateTimeZone = IndexValueConverter.toDate( "2012-02-14T13:35:57.000Z" );
        final DateTime dateTimeZoneExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.UTC );
        assertDateTimeEquals( dateTimeZone, dateTimeZoneExpected );

        final ReadableDateTime dateTimeZoneLowerCase = IndexValueConverter.toDate( "2012-02-14T13:35:57.000Z".toLowerCase() );
        final DateTime dateTimeZoneLowerCaseExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.UTC );
        assertDateTimeEquals( dateTimeZoneLowerCase, dateTimeZoneLowerCaseExpected );

        final ReadableDateTime dateTimeZonePlusOne = IndexValueConverter.toDate( "2012-02-14T13:35:57.000+01:00" );
        final DateTime dateTimeZonePlusOneExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.forOffsetHours( +1 ) );
        assertDateTimeEquals( dateTimeZonePlusOne, dateTimeZonePlusOneExpected );

        final ReadableDateTime dateTimeZoneMinusOne = IndexValueConverter.toDate( "2012-02-14T13:35:57.000-01:00" );
        final DateTime dateTimeZoneMinusOneExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.forOffsetHours( -1 ) );
        assertDateTimeEquals( dateTimeZoneMinusOne, dateTimeZoneMinusOneExpected );

        final ReadableDateTime dateTimeZoneHoursMinutes = IndexValueConverter.toDate( "2012-02-14T13:35:57.000+02:45" );
        final DateTime dateTimeZoneHoursMinutesExpected = new DateTime( 2012, 2, 14, 13, 35, 57, DateTimeZone.forOffsetHoursMinutes( +2, 45 ) );
        assertDateTimeEquals( dateTimeZoneHoursMinutes, dateTimeZoneHoursMinutesExpected );
    }

    private void assertDateTimeEquals( ReadableDateTime expected, ReadableDateTime actual )
    {
        // move to same time-zone if necessary
        if ( !actual.getZone().equals( expected.getZone() ) )
        {
            MutableDateTime actualUpdatedZone = actual.toMutableDateTime();
            actualUpdatedZone.setZone( expected.getZone() );
            actual = actualUpdatedZone.toDateTime();
        }

        assertEquals( expected, actual );
    }

}
