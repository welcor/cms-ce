package com.enonic.cms.core.search.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.builder.ContentIndexDataElement;
import com.enonic.cms.core.search.builder.ContentIndexDataFieldValue;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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
            new ContentIndexDataElement( "number-test", Sets.newHashSet( (Object) new Double( 1 ) ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        // Should contain: String, number, orderby, orderby_number
        assertEquals( 4, allFieldValuesForElement.size() );

        verifyValues( "number-test", allFieldValuesForElement, 1, true );
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

        verifyValues( "string-test", allFieldValuesForElement, 2, false );
    }

    @Test
    public void testNumberSet()
    {
        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "number-test", Sets.newHashSet( (Object) "1", "2" ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 4, allFieldValuesForElement.size() );

        verifyValues( "number-test", allFieldValuesForElement, 2, true );
    }

    @Test
    public void testNumberSetWithEquals()
    {
        ContentIndexDataElement contentIndexDataElement =
            new ContentIndexDataElement( "number-test", Sets.newHashSet( (Object) "1", "1" ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentIndexDataElement.getAllFieldValuesForElement();

        assertEquals( 4, allFieldValuesForElement.size() );

        verifyValues( "number-test", allFieldValuesForElement, 1, true );
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

        verifyValues( "date-test", allFieldValuesForElement, 2, false );
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

    @Test
    public void testDateFieldOrderby()
    {
        final DateTime dateTime = new DateTime( 1975, 8, 1, 12, 00 );

        ContentIndexDataElement contentIndexDataElement = new ContentIndexDataElement( "date-test", Sets.newHashSet( (Object) dateTime ) );

        final Set<ContentIndexDataFieldValue> allFieldValuesForElements = contentIndexDataElement.getAllFieldValuesForElement();


    }


    private void verifyValues( String fieldBaseName, final Set<ContentIndexDataFieldValue> allFieldValuesForElement,
                               int expectedElementsInValueArrays, boolean expectNumericOrderby )
    {
        for ( ContentIndexDataFieldValue value : allFieldValuesForElement )
        {
            if ( value.getValue() instanceof HashSet )
            {
                assertEquals( expectedElementsInValueArrays, ( (HashSet) value.getValue() ).size() );
            }
        }

        assertTrue( containsField( fieldBaseName, allFieldValuesForElement ) );
        assertTrue( containsField( fieldBaseName + "." + IndexFieldNameConstants.ORDERBY_FIELDNAME_POSTFIX, allFieldValuesForElement ) );

        final String numericOrderbyFieldName = fieldBaseName + "." + IndexFieldNameConstants.ORDERBY_NUMERIC_FIELDNAME_POSTFIX;

        if ( expectNumericOrderby )
        {
            assertTrue( containsField( numericOrderbyFieldName, allFieldValuesForElement ) );
        }
        else
        {
            assertFalse( containsField( numericOrderbyFieldName, allFieldValuesForElement ) );
        }
    }

    private boolean containsField( String fieldName, Set<ContentIndexDataFieldValue> allFieldValuesForElements )
    {

        for ( ContentIndexDataFieldValue value : allFieldValuesForElements )
        {
            if ( value.getFieldName().equals( fieldName ) )
            {
                return true;
            }
        }

        return false;
    }

}
