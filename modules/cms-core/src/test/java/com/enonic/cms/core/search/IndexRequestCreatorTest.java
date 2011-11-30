package com.enonic.cms.core.search;

import java.util.Set;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import junit.framework.TestCase;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.index.ContentIndexData;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/28/11
 * Time: 10:04 AM
 */
public class IndexRequestCreatorTest
{

    private IndexRequestCreator indexRequestCreator;


    @Before
    public void setUp()
    {
        indexRequestCreator = new IndexRequestCreator( "TEST_INDEX" );
    }

    @Test
    public void testCreateIndexRequests()
        throws Exception
    {
        ContentIndexData data = new ContentIndexData( new ContentKey( "1" ), buildMetadata( 1, "contentdata" ) );
        data.setCustomdata( buildMetadata( 2, "customdata" ) );
        data.setExtractedBinaryData( buildMetadata( 3, "binarydata" ) );

        Set<IndexRequest> requests = indexRequestCreator.createIndexRequests( data );

        assertEquals( requests.size(), 3 );

        boolean contentWasFirst = false;
        boolean customdataFound = false, binarydataFound = false;

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
            }
            else if ( type.equals( IndexType.Binaries.toString() ) )
            {
                binarydataFound = true;
            }
            else if ( type.equals( IndexType.Customdata.toString() ) )
            {
                customdataFound = true;
            }
        }

        assertTrue( contentWasFirst && binarydataFound && customdataFound );

    }


    private XContentBuilder buildMetadata( Integer key, String data )
        throws Exception
    {

        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        result.field( "key", key );
        result.field( "data", data );
        result.endObject();

        return result;
    }

}