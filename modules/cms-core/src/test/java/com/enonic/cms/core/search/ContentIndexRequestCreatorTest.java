package com.enonic.cms.core.search;

import java.util.Set;

import org.elasticsearch.action.index.IndexRequest;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.builder.ContentIndexData;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/28/11
 * Time: 10:04 AM
 */
public class ContentIndexRequestCreatorTest
{

    private ContentIndexRequestCreator contentIndexRequestCreator;


    @Before
    public void setUp()
    {
        contentIndexRequestCreator = new ContentIndexRequestCreator();
    }

    @Test
    public void testCreateIndexRequests()
        throws Exception
    {
        ContentIndexData data = new ContentIndexData( new ContentKey( "1" ) );
        data.addContentIndexDataElement( "contentdata", 1 );
        data.addBinaryData( "attachment", "This is a test-representation of binary data" );

        Set<IndexRequest> requests = contentIndexRequestCreator.createIndexRequests( "TEST_INDEX", data );

        assertEquals( 2, requests.size() );

        boolean contentWasFirst = false;
        boolean binarydataFound = false;
        String contentId = null;

        for ( IndexRequest request : requests )
        {
            final String type = request.type();

            if ( !contentWasFirst && !type.equals( IndexType.Content.toString() ) )
            {
                fail();
            }
            else if ( type.equals( IndexType.Content.toString() ) )
            {
                contentWasFirst = true;
                assertTrue( "ContentData should not have parent", request.parent() == null );
                contentId = request.id();
            }
            else if ( type.equals( IndexType.Binaries.toString() ) )
            {
                binarydataFound = true;
                assertTrue( "Binary data should have parent, pointing to content", request.parent() != null );
                assertEquals( contentId, request.parent() );
                assertEquals( contentId, request.id() );
            }
        }

        assertTrue( contentWasFirst && binarydataFound );
    }

    @Test
    public void testOnlyCreateBinaryRequestIfAnyBinaryData()
        throws Exception
    {
        ContentIndexData data = new ContentIndexData( new ContentKey( "1" ) );
        data.addContentIndexDataElement( "contentdata", 1 );

        Set<IndexRequest> requests = contentIndexRequestCreator.createIndexRequests( "TEST_INDEX", data );

        assertEquals( 1, requests.size() );
    }
}
