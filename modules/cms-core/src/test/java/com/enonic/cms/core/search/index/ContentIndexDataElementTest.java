package com.enonic.cms.core.search.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Sets;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ContentIndexDataElementTest
{


    @Test
    public void testCreateStringElement()
    {
        ContentIndexDataElement contentIndexDataElement = new ContentIndexDataElement( "date-test", Sets.newHashSet( (Object) "test" ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        // Should contain: String, order
        assertEquals( 2, allFieldValuesForElement.size() );
    }

    @Test
    public void testCreateDateElement()
    {
        final Date time = Calendar.getInstance().getTime();
        ContentIndexDataElement contentIndexDataElement = new ContentIndexDataElement( "date-test", Sets.newHashSet( (Object) time ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        // Should contain: String, date, order
        assertEquals( 3, allFieldValuesForElement.size() );

    }

    @Test
    public void testCreateNumberElement()
    {
        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "date-test", Sets.newHashSet( (Object) new Double( 1 ) ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        // Should contain: String, number, order
        assertEquals( 3, allFieldValuesForElement.size() );
    }

    @Test
    public void testEmptyValue()
    {
        ContentIndexDataElement contentIndexDataElement = new ContentIndexDataElement( "date-test", null );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 0, allFieldValuesForElement.size() );
    }

    @Test
    public void testStringSet()
    {
        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "string-test", Sets.newHashSet( (Object) "test1", "test2" ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 2, allFieldValuesForElement.size() );

        verifyValues( allFieldValuesForElement, 2 );
    }

    @Test
    public void testNumberSet()
    {
        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "number-test", Sets.newHashSet( (Object) "1", "2" ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 3, allFieldValuesForElement.size() );

        verifyValues( allFieldValuesForElement, 2 );
    }

    private void verifyValues( final Set<ContentIndexDataFieldValue> allFieldValuesForElement, int expectedElements )
    {
        for ( ContentIndexDataFieldValue value : allFieldValuesForElement )
        {
            if ( value.getValue() instanceof HashSet )
            {
                assertEquals( expectedElements, ( (HashSet) value.getValue() ).size() );
            }
            else
            {
                assertTrue( StringUtils.endsWith( value.getFieldName().toString(), "orderby" ) );
            }
        }
    }

    @Test
    public void testNumberSetWithEquals()
    {
        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "number-test", Sets.newHashSet( (Object) "1", "1" ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 3, allFieldValuesForElement.size() );

        verifyValues( allFieldValuesForElement, 1 );
    }

    @Test
    public void testDateSet()
    {
        final Calendar now = Calendar.getInstance();
        final Date time = now.getTime();
        now.add( 1, 1 );
        final Date otherTime = now.getTime();

        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "date-test", Sets.newHashSet( (Object) time, otherTime ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 3, allFieldValuesForElement.size() );

        verifyValues( allFieldValuesForElement, 2 );
    }

    @Ignore // Format to be decided
    @Test
    public void testValidStringFormatForDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-DD'T'HH:mm:ss" );

        final DateTime dateTime = new DateTime( 1975, 8, 1, 12, 00 );

        ContentIndexDataElement contentIndexDataElement = new ContentIndexDataElement( "date-test", Sets.newHashSet( (Object) dateTime ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElements = contentIndexDataElement.getAllFieldValuesForElement();

        // Should contain: String, date, order
        assertEquals( 3, allFieldValuesForElements.size() );

        for ( ContentIndexDataFieldValue allFieldValuesForElement : allFieldValuesForElements )
        {
            if ( allFieldValuesForElement.getFieldName().toString().equals( "date-test" ) )
            {
                final String dateStringValue = allFieldValuesForElement.getValue().toString();

                try
                {

                    simpleDateFormat.parse( dateStringValue );
                }
                catch ( ParseException e )
                {
                    fail( "Not correct format: " + dateStringValue );
                }

            }
        }
    }

}
