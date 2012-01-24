package com.enonic.cms.core.search.builder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/24/12
 * Time: 2:22 PM
 */
public class IndexFieldNameCreatorTest
{
    @Test
    public void testNormalize()
    {
        String result = IndexFieldNameCreator.normalizeFieldName( "data/test" );
        assertEquals( "data_test", result );

        result = IndexFieldNameCreator.normalizeFieldName( "data/test/@key" );
        assertEquals( "data_test_key", result );

        result = IndexFieldNameCreator.normalizeFieldName( "data.test" );
        assertEquals( "data_test", result );

        result = IndexFieldNameCreator.normalizeFieldName( "data/test.instance/@key" );
        assertEquals( "data_test_instance_key", result );
    }

    @Test
    public void testNormalizeEmpty()
    {
        String result = IndexFieldNameCreator.normalizeFieldName( "" );
        assertEquals( "", result );

        result = IndexFieldNameCreator.normalizeFieldName( null );
        assertEquals( "", result );
    }

}
