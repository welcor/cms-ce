package com.enonic.cms.core.search.query;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/14/12
 * Time: 7:53 AM
 */
public class QueryValueTest
{
    @Test
    public void testInt()
    {
        QueryValue queryValue = new QueryValue( 1 );

        assertEquals( 1.0, queryValue.getDoubleValue() );
        assertEquals( "1", queryValue.getStringValueNormalized() );

        assertFalse( queryValue.isEmpty() );
        assertFalse( queryValue.isValidDateString() );
        assertTrue( queryValue.isNumeric() );
    }

    @Test
    public void testIntAsString()
    {
        QueryValue queryValue = new QueryValue( "1" );

        assertFalse( queryValue.isEmpty() );
        assertFalse( queryValue.isValidDateString() );
        assertFalse( queryValue.isNumeric() );

        assertEquals( "1", queryValue.getStringValueNormalized() );
    }

    @Test
    public void testDateFormat()
    {
        QueryValue queryValue = new QueryValue( "01-08-1975" );

        assertTrue( queryValue.isValidDateString() );

    }

    @Test
    public void testNormalizedString()
    {

        QueryValue queryValue = new QueryValue( "AbCdE" );

        Assert.assertEquals( "abcde", queryValue.getStringValueNormalized() );

    }

}
