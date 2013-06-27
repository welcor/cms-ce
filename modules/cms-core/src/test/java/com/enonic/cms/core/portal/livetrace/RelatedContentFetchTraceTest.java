/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RelatedContentFetchTraceTest
{
    @Test
    public void testGetChildrenFetches()
        throws Exception
    {
        RelatedContentFetchTrace trace = new RelatedContentFetchTrace();

        assertEquals( "", trace.getChildrenFetches() );

        trace.setChildrenFetch( 1, 123 );

        assertEquals( "123", trace.getChildrenFetches() );
        trace.setChildrenFetch( 2, 555 );

        assertEquals( "123 -> 555", trace.getChildrenFetches() );
    }


    @Test
    public void testGetParentFetches()
        throws Exception
    {
        RelatedContentFetchTrace trace = new RelatedContentFetchTrace();

        assertEquals( "", trace.getParentFetches() );

        trace.setParentFetch( 1, 123 );
        assertEquals( "123", trace.getParentFetches() );

        trace.setParentFetch( 2, 555 );
        assertEquals( "123 -> 555", trace.getParentFetches() );

        trace.setParentFetch( 3, 321 );
        assertEquals( "123 -> 555 -> 321", trace.getParentFetches() );
    }
}
