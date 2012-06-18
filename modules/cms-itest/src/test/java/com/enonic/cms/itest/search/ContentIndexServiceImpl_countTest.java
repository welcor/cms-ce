package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

public class ContentIndexServiceImpl_countTest
    extends ContentIndexServiceTestBase
{


 @Test
    public void testLargeCountNumber()
        throws Exception
    {
        indexContent( 1 );
        indexContent( 2 );
        indexContent( 3 );
        indexContent( 4 );
        indexContent( 5 );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "title = 'test'" );
        query.setCount( Integer.MAX_VALUE );

        final ContentResultSet result = contentIndexService.query( query );

        // Will fail with exception if count not working
    }

    private void indexContent( int contentKey )
    {
        ContentDocument contentDoc = new ContentDocument( new ContentKey( contentKey ) );
        contentDoc.setTitle( "test" );
        contentDoc.setContentTypeKey( new ContentTypeKey( 1 ) );
        contentDoc.setContentTypeName( "testContentType" );

        contentIndexService.index( contentDoc );
    }

}
