/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class QueryValueResolverTest
{
    @Test
    public void testToValue_string()
    {
        Expression expression = new ValueExpr( "100.0" );

        final QueryValue actual = QueryValueFactory.resolveQueryValues( expression )[0];

        assertEquals( "100.0", actual.getStringValueNormalized() );
    }

    @Test
    public void testToValue_int()
    {
        Expression expression = new ValueExpr( 100.0 );

        final QueryValue actual = QueryValueFactory.resolveQueryValues( expression )[0];
        assertEquals( 100.0, actual.getNumericValue() );
    }

    @Test
    public void testToValues_else()
    {
        Expression expression = new FieldExpr( "" );

        assertNotNull( QueryValueFactory.resolveQueryValues( expression ) );

        final int length = QueryValueFactory.resolveQueryValues( expression ).length;
        assertEquals( 0, length );
    }

    @Test
    public void testDateValue()
    {
        Expression expression = new ValueExpr( new DateTime( 2012, 02, 14, 11, 5, 0, DateTimeZone.UTC ) );
        final QueryValue[] queryValues = QueryValueFactory.resolveQueryValues( expression );

        final QueryValue actual = QueryValueFactory.resolveQueryValues( expression )[0];

        assertTrue( actual.isDateTime() );

        // one hour behind because of timezone normalization
        assertDateTimeEquals( "2012-02-14T11:05:00.000Z", actual.getDateTime().toString() );
    }

    @Test
    public void testDateAsStringValue()
    {
        Expression expression = new ValueExpr( "2012-02-14T12:05:00.000Z" );
        final QueryValue[] queryValues = QueryValueFactory.resolveQueryValues( expression );

        final QueryValue actual = QueryValueFactory.resolveQueryValues( expression )[0];

        assertFalse( actual.isDateTime() );
    }

    private void assertDateTimeEquals( final String expectedDateTime, final String actualDateTime )
    {
        DateTime expected = DateTime.parse( expectedDateTime );
        DateTime actual = DateTime.parse( actualDateTime );

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
