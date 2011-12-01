package com.enonic.cms.core.search.query;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import junit.framework.TestCase;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/1/11
 * Time: 2:39 PM
 */
public class QueryFieldNameResolverTest
{


    @Before
    public void setUp()
    {

    }

    @Test
    public void testStuff()
    {
        String normalized = QueryFieldNameResolver.normalizeFieldName( "category/@key" );
        assertEquals( "category_key", normalized );


    }
}
