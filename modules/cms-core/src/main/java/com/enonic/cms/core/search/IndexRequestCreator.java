package com.enonic.cms.core.search;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.cms.core.search.index.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/28/11
 * Time: 10:00 AM
 */
public class IndexRequestCreator
{
    private final String indexName;

    private final IndexRequestComparator comparator = new IndexRequestComparator();

    public IndexRequestCreator( String indexName )
    {
        this.indexName = indexName;
    }

    public Set<IndexRequest> createIndexRequests( ContentIndexData contentIndexData )
    {
        Set<IndexRequest> indexRequests = new TreeSet<IndexRequest>( comparator );

        final String id = contentIndexData.getKey().toString();

        if ( contentIndexData.getMetadata() != null )
        {
            indexRequests.add( createIndexRequest( id, contentIndexData.getMetadata(), IndexType.Content, null ) );
        }

        if ( contentIndexData.getCustomdata() != null )
        {
            indexRequests.add( createIndexRequest( id, contentIndexData.getCustomdata(), IndexType.Customdata, id ) );
        }

        if ( contentIndexData.getExtractedBinaryData() != null )
        {
            indexRequests.add( createIndexRequest( id, contentIndexData.getExtractedBinaryData(), IndexType.Binaries, id ) );
        }

        return indexRequests;
    }


    private IndexRequest createIndexRequest( String id, XContentBuilder data, IndexType indexType, String parent )
    {
        IndexRequest request = new IndexRequest( indexName ).type( indexType.toString() ).id( id ).source( data );
        if ( parent != null )
        {
            request.parent( parent );
        }

        return request;
    }

    private class IndexRequestComparator
        implements Comparator<IndexRequest>
    {
        public int compare( IndexRequest indexRequest, IndexRequest indexRequest1 )
        {
            if ( indexRequest.equals( indexRequest1 ) )
            {
                return 0;
            }

            if ( indexRequest.type().equals( IndexType.Content.toString() ) )
            {
                return -1;
            }

            return 1;

        }
    }

}
