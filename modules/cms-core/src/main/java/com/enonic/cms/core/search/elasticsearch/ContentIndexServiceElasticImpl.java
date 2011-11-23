package com.enonic.cms.core.search.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.search.index.ContentIndexService;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:41 PM
 */
public class ContentIndexServiceElasticImpl
    extends ElasticSearchConstants
    implements ContentIndexService
{
    private IndexMappingProvider mappingProvider;

    private Client client;

    public void index( final ContentIndexData... contentIndexDatas )
    {
        if ( contentIndexDatas == null )
        {
            return;
        }

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for ( ContentIndexData contentIndexData : contentIndexDatas )
        {
            addIndexRequests( bulkRequest, createIndexRequests( contentIndexData ) );
        }
    }

    private void addIndexRequests( BulkRequestBuilder bulkRequest, List<IndexRequest> requests )
    {
        for ( IndexRequest request : requests )
        {
            bulkRequest.add( request );
        }
    }

    private List<IndexRequest> createIndexRequests( ContentIndexData contentIndexData )
    {
        List<IndexRequest> indexRequests = new ArrayList<IndexRequest>();
        final String id = contentIndexData.getKey().toString();

        if ( contentIndexData.getMetadata() != null )
        {
            indexRequests.add( createIndexRequest( id, contentIndexData.getMetadata(), IndexType.Content ) );
        }

        if ( contentIndexData.getCustomdata() != null )
        {
            indexRequests.add( createIndexRequest( id, contentIndexData.getCustomdata(), IndexType.Customdata ) );
        }

        if ( contentIndexData.getExtractedBinaryData() != null )
        {
            indexRequests.add( createIndexRequest( id, contentIndexData.getExtractedBinaryData(), IndexType.Binaries ) );
        }

        return indexRequests;
    }

    private IndexRequest createIndexRequest( String id, XContentBuilder data, IndexType indexType )
    {
        return new IndexRequest( CONTENT_INDEX_NAME ).type( indexType.toString() ).id( id ).source( data );
    }

    public void delete( final ContentKey... contentKeys )
    {

        for ( ContentKey contentKey : contentKeys )
        {
            doDelete( CONTENT_INDEX_NAME, contentKey.toString(), IndexType.Customdata.toString(), IndexType.Binaries.toString(),
                      IndexType.Content.toString() );
        }
    }

    private void doDelete( final String indexName, final String id, final IndexType indexType )
    {
        final DeleteRequest deleteRequest = new DeleteRequest().index( indexName ).type( indexType.toString() ).id( id );
        DeleteResponse resp = this.client.delete( deleteRequest ).actionGet();
    }

    private void doDelete( final String indexName, final String id, final String... indexTypes )
    {
        // TODO: create query with _parent or _id
        final DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest( indexName ).types( indexTypes ).query( "" );
        DeleteByQueryResponse resp = this.client.deleteByQuery( deleteByQueryRequest ).actionGet();
    }

    public void delete( CategoryKey... categoryKeys )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete( ContentTypeKey... contentTypeKeys )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update( ContentKey... contentKeys )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update( CategoryKey... categoryKeys )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update( ContentTypeKey... contentTypeKeys )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void addMapping()
    {
        doAddMapping( mappingProvider.getMapping( CONTENT_INDEX_NAME, IndexType.Content ) );
        doAddMapping( mappingProvider.getMapping( CONTENT_INDEX_NAME, IndexType.Customdata ) );
        doAddMapping( mappingProvider.getMapping( CONTENT_INDEX_NAME, IndexType.Binaries ) );

    }

    private void doAddMapping( String contentMapping )
    {
        PutMappingRequest putMappingRequest = new PutMappingRequest( CONTENT_INDEX_NAME );
        putMappingRequest.source( contentMapping );

        client.admin().indices().putMapping( putMappingRequest ).actionGet();
    }

    @Autowired
    public void setMappingProvider( IndexMappingProvider mappingProvider )
    {
        this.mappingProvider = mappingProvider;
    }

    @Autowired
    public void setClient( Client client )
    {
        this.client = client;
    }
}
