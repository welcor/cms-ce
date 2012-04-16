package com.enonic.cms.core.search.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class QueryPathResolverTest
{
    @Test
    public void testCreateQueryPath()
    {
        QueryField startDataField = QueryPathResolver.resolveQueryPath( "data" );
        assertEquals( false, startDataField.doRenderAsHasChildQuery() );
        assertEquals( "content", startDataField.getIndexType().toString() );

        QueryField startAttachmentField = QueryPathResolver.resolveQueryPath( "attachment" );
        assertEquals( true, startAttachmentField.doRenderAsHasChildQuery() );
        assertEquals( "binaries", startAttachmentField.getIndexType().toString() );
    }
}
