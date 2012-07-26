package com.enonic.cms.core.search.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class QueryPathResolverTest
{
    @Test
    public void testCreateQueryPath()
    {
        QueryField startDataField = QueryFieldFactory.resolveQueryField( "data" );
        assertEquals( false, startDataField.doRenderAsHasChildQuery() );
        assertEquals( "content", startDataField.getIndexType().toString() );

        QueryField startAttachmentField = QueryFieldFactory.resolveQueryField( "attachment" );
        assertEquals( true, startAttachmentField.doRenderAsHasChildQuery() );
        assertEquals( "binaries", startAttachmentField.getIndexType().toString() );
    }
}
