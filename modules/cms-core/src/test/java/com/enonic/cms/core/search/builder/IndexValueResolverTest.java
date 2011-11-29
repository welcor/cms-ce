package com.enonic.cms.core.search.builder;

import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;
import com.enonic.cms.core.search.builder.IndexValueResolver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 12:27 PM
 */
public class IndexValueResolverTest
{


    @Test
    public void testIndexValueResolverBasics()
    {
        String result = IndexValueResolver.getOrderByValue( 123 );
        assertNotNull( result );

        result = IndexValueResolver.getOrderByValue( new Double( 123 ) );
        assertNotNull( result );

        result = IndexValueResolver.getOrderByValue( new Float( 123 ) );
        assertNotNull( result );
    }

    @Test
    public void testExpressionToValue()
    {
        Object result = IndexValueResolver.toValue( new ValueExpr( "123" ) );
        assertTrue( result instanceof String );

        result = IndexValueResolver.toValue( new ValueExpr( 123 ) );



    }

    @Test
    public void testBorderLineIssues()
    {
        String result = IndexValueResolver.getOrderByValue( null );
        assertNull( result );
    }

}
