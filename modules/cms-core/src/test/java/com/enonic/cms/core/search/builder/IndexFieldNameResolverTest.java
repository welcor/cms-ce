package com.enonic.cms.core.search.builder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/24/12
 * Time: 2:22 PM
 */
public class IndexFieldNameResolverTest
{
    @Test
    public void testNormalize()
    {
        String result = IndexFieldNameResolver.normalizeFieldName( "data/test" );
        assertEquals( "data_test", result );

        result = IndexFieldNameResolver.normalizeFieldName( "data/test/@key" );
        assertEquals( "data_test_key", result );

        result = IndexFieldNameResolver.normalizeFieldName( "data.test" );
        assertEquals( "data_test", result );

        result = IndexFieldNameResolver.normalizeFieldName( "data/test.instance/@key" );
        assertEquals( "data_test_instance_key", result );
    }

    @Test
    public void testNormalizeEmpty()
    {
        String result = IndexFieldNameResolver.normalizeFieldName( "" );
        assertEquals( "", result );

        result = IndexFieldNameResolver.normalizeFieldName( null );
        assertEquals( "", result );
    }

}
