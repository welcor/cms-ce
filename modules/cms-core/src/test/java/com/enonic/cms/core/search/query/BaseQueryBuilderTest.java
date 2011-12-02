package com.enonic.cms.core.search.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BaseQueryBuilderTest
{

    @Test
    public void testStuff()
    {

        assertEquals( 1.0, BaseQueryBuilder.getNumericValue( "1" ) );

        assertEquals( 1.0, BaseQueryBuilder.getNumericValue( 1 ) );

        assertEquals( 1.0, BaseQueryBuilder.getNumericValue( 1.0 ) );

        assertEquals( null, BaseQueryBuilder.getNumericValue( null ) );

        assertEquals( null, BaseQueryBuilder.getNumericValue( "abc" ) );

    }
}
