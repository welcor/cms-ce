package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_errorHandling
    extends ContentIndexServiceTestBase
{

    @Test
    public void testEmptyInQuery()
        throws Exception
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "contentType in ()" );
        query.setCount( 10 );

        final ContentResultSet result = contentIndexService.query( query );

        assertEquals( 0, result.getLength() );
        assertEquals( 1, result.getErrors().size() );
    }
}
