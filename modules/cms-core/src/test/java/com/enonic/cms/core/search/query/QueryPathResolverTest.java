package com.enonic.cms.core.search.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class QueryPathResolverTest
{
    @Test
    public void testCreateQueryPath()
    {
        QueryPath startDataPath = QueryPathResolver.resolveQueryPath( "data" );
        assertEquals( false, startDataPath.doRenderAsHasChildQuery() );
        assertEquals( "content", startDataPath.getIndexType().toString() );

        QueryPath startAttachmentPath = QueryPathResolver.resolveQueryPath( "attachment" );
        assertEquals( true, startAttachmentPath.doRenderAsHasChildQuery() );
        assertEquals( "binaries", startAttachmentPath.getIndexType().toString() );
    }
}
