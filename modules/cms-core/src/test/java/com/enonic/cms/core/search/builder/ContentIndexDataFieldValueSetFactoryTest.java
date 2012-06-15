package com.enonic.cms.core.search.builder;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ContentIndexDataFieldValueSetFactoryTest
{
    @Test
    public void testNoValuesGivesZeroElements()
    {
        ContentIndexDataElement element = new ContentIndexDataElement();
        element.setFieldBaseName( "fieldBaseName" );

        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = ContentIndexDataFieldValueSetFactory.create( element );

        assertEquals( 0, contentIndexDataFieldAndValues.size() );
    }

    @Test
    public void testOnlyOrderByValueGivesZeroElement()
    {
        ContentIndexDataElement element = new ContentIndexDataElement();
        element.setOrderBy( "orderBy" );
        element.setFieldBaseName( "fieldBaseName" );

        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = ContentIndexDataFieldValueSetFactory.create( element );

        assertEquals( 0, contentIndexDataFieldAndValues.size() );
    }

    @Test
    public void testStringValue()
    {
        ContentIndexDataElement element = new ContentIndexDataElement();
        element.setOrderBy( "orderBy" );
        element.setFieldBaseName( "fieldBaseName" );
        element.addStringValue( "StringValue" );

        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = ContentIndexDataFieldValueSetFactory.create( element );

        assertEquals( 2, contentIndexDataFieldAndValues.size() );
    }

    @Test
    public void testNumericValue()
    {
        ContentIndexDataElement element = new ContentIndexDataElement();
        element.setOrderBy( "orderBy" );
        element.setFieldBaseName( "fieldname" );
        element.addNumericValue( new Double( 1 ) );

        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = ContentIndexDataFieldValueSetFactory.create( element );

        assertEquals( 2, contentIndexDataFieldAndValues.size() );
    }

    @Test
    public void testDateValue()
    {
        ContentIndexDataElement element = new ContentIndexDataElement();
        element.setOrderBy( "orderBy" );
        element.setFieldBaseName( "fieldname" );
        element.addDateValue( Calendar.getInstance().getTime() );

        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = ContentIndexDataFieldValueSetFactory.create( element );

        assertEquals( 2, contentIndexDataFieldAndValues.size() );
    }

    @Test
    public void testSeveralStringValues()
    {
        ContentIndexDataElement element = new ContentIndexDataElement();
        element.setOrderBy( "orderBy" );
        element.setFieldBaseName( "fieldBaseName" );
        element.addStringValue( "StringValue" );
        element.addStringValue( "StringValue2" );
        element.addStringValue( "StringValue3" );
        element.addStringValue( "StringValue4" );

        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = ContentIndexDataFieldValueSetFactory.create( element );

        assertEquals( 2, contentIndexDataFieldAndValues.size() );

        boolean found = false;

        final Iterator<ContentIndexDataFieldAndValue> iterator = contentIndexDataFieldAndValues.iterator();

        while ( iterator.hasNext() )
        {
            final ContentIndexDataFieldAndValue fieldAndValue = iterator.next();

            if ( fieldAndValue.getFieldName().equals( "fieldBaseName" ) )
            {
                found = true;
                final Object value = fieldAndValue.getValue();
                assertTrue( value instanceof Set );
                final Set<String> stringValues = (Set<String>) value;
                assertEquals( 4, stringValues.size() );
            }
        }

        assertTrue( found );
    }

}
