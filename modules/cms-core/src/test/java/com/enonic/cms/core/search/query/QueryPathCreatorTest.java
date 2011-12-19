package com.enonic.cms.core.search.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class QueryPathCreatorTest
{
    @Test
    public void testCreateQueryPath()
    {
        QueryPath startDataPath = QueryPathCreator.createQueryPath( "data" );
        assertEquals( false, startDataPath.doRenderAsHasChildQuery() );
        assertEquals( "content", startDataPath.getIndexType().toString() );

        QueryPath startAttachmentPath = QueryPathCreator.createQueryPath( "attachment" );
        assertEquals( true, startAttachmentPath.doRenderAsHasChildQuery() );
        assertEquals( "binaries", startAttachmentPath.getIndexType().toString() );
    }
}
