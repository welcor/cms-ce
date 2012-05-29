package com.enonic.cms.core.search;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.search.builder.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/28/11
 * Time: 10:00 AM
 */
@Component
final class ContentIndexRequestCreator
{
    private final IndexRequestComparator comparator = new IndexRequestComparator();

    public Set<IndexRequest> createIndexRequests( String indexName, ContentIndexData contentIndexData )
    {
        Set<IndexRequest> indexRequests = new TreeSet<IndexRequest>( comparator );

        addRequestsForBinaryData( indexName, contentIndexData, indexRequests );

        addRequestsForContentData( indexName, contentIndexData, indexRequests );

        return indexRequests;
    }

    private void addRequestsForContentData( final String indexName, final ContentIndexData contentIndexData,
                                            final Set<IndexRequest> indexRequests )
    {
        final String id = contentIndexData.getKey().toString();
        doAddRequests( indexName, contentIndexData.buildContentDataJson(), indexRequests, id, null, IndexType.Content );
    }

    private void addRequestsForBinaryData( final String indexName, final ContentIndexData contentIndexData,
                                           final Set<IndexRequest> indexRequests )
    {

        if ( !contentIndexData.hasBinaryData() )
        {
            return;
        }

        final String parentId = contentIndexData.getKey().toString();

        doAddRequests( indexName, contentIndexData.buildBinaryDataJson(), indexRequests, parentId, parentId, IndexType.Binaries );
    }

    private void doAddRequests( final String indexName, final XContentBuilder xContentBuilder, final Set<IndexRequest> indexRequests,
                                final String id, final String parentId, IndexType indexType )
    {
        if ( xContentBuilder != null )
        {
            final IndexRequest indexRequest = createIndexRequest( indexName, id, xContentBuilder, indexType, parentId );
            indexRequests.add( indexRequest );
        }
    }

    private IndexRequest createIndexRequest( String indexName, String id, XContentBuilder data, IndexType indexType, String parent )
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
