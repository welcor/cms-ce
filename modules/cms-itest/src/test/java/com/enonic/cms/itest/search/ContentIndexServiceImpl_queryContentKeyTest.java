/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/13/12
 * Time: 3:15 PM
 */
public class ContentIndexServiceImpl_queryContentKeyTest
    extends ContentIndexServiceTestBase

{
    @Test
    public void testSingleContentKeyQuery()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "key = 1322" );

        ContentResultSet res1 = contentIndexService.query( query );
        assertEquals( 1, res1.getLength() );


    }


}
