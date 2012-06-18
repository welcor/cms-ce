package com.enonic.cms.core.search.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.ElasticSearchFormatter;
import com.enonic.cms.core.search.builder.ContentIndexDataElement;
import com.enonic.cms.core.search.builder.ContentIndexDataElementFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

public class ContentIndexDataElementFactoryTest
{

    @Test
    public void testNullValue()
    {
        ContentIndexDataElement contentIndexDataElement = ContentIndexDataElementFactory.create( "date-test", null );

        assertNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 0, contentIndexDataElement.getStringValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
    }

    @Test
    public void testSetWithNullValue()
    {
        final HashSet<Object> values = Sets.newHashSet();
        values.add( null );

        ContentIndexDataElement contentIndexDataElement = ContentIndexDataElementFactory.create( "date-test", values );

        assertNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 0, contentIndexDataElement.getStringValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
    }


    @Test
    public void testCreateStringElement()
    {
        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "date-test", Sets.newHashSet( (Object) "test" ) );

        contentIndexDataElement.setFieldBaseName( "date-test" );
        contentIndexDataElement.addStringValue( "test" );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 1, contentIndexDataElement.getStringValues().size() );
    }

    @Test
    public void testCreateDateElement()
    {
        final Date time = Calendar.getInstance().getTime();
        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "date-test", Sets.newHashSet( (Object) time ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 1, contentIndexDataElement.getStringValues().size() );
        assertEquals( 1, contentIndexDataElement.getDateTimeValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
    }

    @Test
    public void testCreateStringWithDateFormat()
    {
        final Date time = Calendar.getInstance().getTime();

        final String dateAsString = ElasticSearchFormatter.formatDateAsStringFull( time );

        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "date-test", Sets.newHashSet( (Object) dateAsString ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 1, contentIndexDataElement.getStringValues().size() );
        assertEquals( 1, contentIndexDataElement.getDateTimeValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
    }

    @Test
    public void testCreateNumberElement()
    {
        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "number-test", Sets.newHashSet( (Object) new Double( 1 ) ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 1, contentIndexDataElement.getStringValues().size() );
        assertEquals( 1, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
    }

    @Test
    public void testEmptyValue()
    {
        ContentIndexDataElement contentIndexDataElement = ContentIndexDataElementFactory.create( "date-test", null );

        assertNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 0, contentIndexDataElement.getStringValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
    }

    @Test
    public void testStringSet()
    {
        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "string-test", Sets.newHashSet( (Object) "test1", "test2" ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 2, contentIndexDataElement.getStringValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
    }

    @Test
    public void testNumberSet()
    {
        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "number-test", Sets.newHashSet( (Object) "1", "2" ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 2, contentIndexDataElement.getStringValues().size() );
        assertEquals( 2, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
    }

    @Test
    public void testNumberSetWithEquals()
    {
        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "number-test", Sets.newHashSet( (Object) "1", "1" ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 1, contentIndexDataElement.getStringValues().size() );
        assertEquals( 1, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 0, contentIndexDataElement.getDateTimeValues().size() );
    }

    @Test
    public void testDateSet()
    {
        final Calendar now = Calendar.getInstance();
        final Date time = now.getTime();
        now.add( 1, 1 );
        final Date otherTime = now.getTime();

        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "date-test", Sets.newHashSet( (Object) time, otherTime ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 2, contentIndexDataElement.getStringValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 2, contentIndexDataElement.getDateTimeValues().size() );
    }

    @Test
    public void testValidStringFormatForDateAsSet()
    {
        SimpleDateFormat expectedFormat = ElasticSearchFormatter.elasticsearchSimpleDateFormat;

        final DateTime dateTime = new DateTime( 1975, 8, 1, 12, 00 );

        ContentIndexDataElement contentIndexDataElement =
            ContentIndexDataElementFactory.create( "date-test", Sets.newHashSet( (Object) dateTime.toDate() ) );

        assertNotNull( contentIndexDataElement.getOrderBy() );
        assertEquals( 1, contentIndexDataElement.getStringValues().size() );
        assertEquals( 0, contentIndexDataElement.getNumericValues().size() );
        assertEquals( 1, contentIndexDataElement.getDateTimeValues().size() );

        final String dateStringValue = contentIndexDataElement.getStringValues().iterator().next();

        try
        {
            expectedFormat.parse( dateStringValue );
        }
        catch ( ParseException e )
        {
            fail(
                "incorrect date format in string-representation of date: " + dateStringValue + ", expected: " + expectedFormat.toString() );
        }
    }

}
