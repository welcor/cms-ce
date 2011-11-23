package com.enonic.cms.core.search.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import junit.framework.TestCase;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

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
    public void testBorderLineIssues()
    {
        String result = IndexValueResolver.getOrderByValue( null );
        assertNull( result );
    }

}
