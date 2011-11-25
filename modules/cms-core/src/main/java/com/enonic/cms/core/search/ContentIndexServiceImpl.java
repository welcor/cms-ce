package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.IndexMappingProvider;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.search.index.ContentIndexService;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:41 PM
 */
public class ContentIndexServiceImpl
    implements ContentIndexService
{
    public final static String INDEX_NAME = "cms";

    private IndexMappingProvider mappingProvider;

    private Client client;

    public void createIndex()
        throws Exception
    {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest( INDEX_NAME );
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        settings.loadFromSource( IndexAnalyzerSettingsBuilder.buildAnalyserSettings() );
        //TODO: Other settings

        createIndexRequest.settings( settings );
        client.admin().indices().create( createIndexRequest ).actionGet();

        addMapping();
    }

    public void index( final ContentIndexData contentIndexData )
    {
        List<IndexRequest> indexRequests = createIndexRequests( contentIndexData );

        for ( IndexRequest indexRequest : indexRequests )
        {
            doIndex( indexRequest );
        }
    }

    public void index( final ContentIndexData... contentIndexDatas )
    {
        doBulkIndex( contentIndexDatas );
    }

    private void doBulkIndex( ContentIndexData[] contentIndexDatas )
    {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for ( ContentIndexData contentIndexData : contentIndexDatas )
        {
            addIndexRequests( bulkRequest, createIndexRequests( contentIndexData ) );
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();

        if ( bulkResponse.hasFailures() )
        {
            //TODO: Handle exception
        }
    }

    private List<IndexRequest> createIndexRequests( ContentIndexData contentIndexData )
    {
        List<IndexRequest> indexRequests = new ArrayList<IndexRequest>();
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
        IndexRequest request = new IndexRequest( INDEX_NAME ).type( indexType.toString() ).id( id ).source( data );
        if ( parent != null )
        {
            request.parent( parent );
        }

        return request;
    }

    private void doIndex( IndexRequest request )
    {
        this.client.index( request ).actionGet();
    }


    private void addIndexRequests( BulkRequestBuilder bulkRequest, List<IndexRequest> requests )
    {
        for ( IndexRequest request : requests )
        {
            bulkRequest.add( request );
        }
    }


    public void delete( final ContentKey... contentKeys )
    {

        for ( ContentKey contentKey : contentKeys )
        {
            doDelete( INDEX_NAME, contentKey.toString(), IndexType.Customdata.toString(), IndexType.Binaries.toString(),
                      IndexType.Content.toString() );
        }
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

    private void initalizeIndex( boolean force )
        throws Exception
    {

        final boolean indexExists = indexExists();

        if ( indexExists && !force )
        {
            return;
        }
        else if ( indexExists )
        {
            deleteIndex();
        }

        createIndex();
        addMapping();


    }

    public DeleteIndexResponse deleteIndex()
        throws Exception
    {
        return this.client.admin().indices().delete( new DeleteIndexRequest( INDEX_NAME ) ).actionGet();
    }


    public boolean indexExists()
    {
        try
        {
            this.client.admin().indices().status( new IndicesStatusRequest( INDEX_NAME ) ).actionGet();
            return true;
        }
        catch ( ElasticSearchException e )
        {
            return false;
        }
    }

    public void addMapping()
    {
        doAddMapping( INDEX_NAME, IndexType.Content );
        doAddMapping( INDEX_NAME, IndexType.Binaries );
        doAddMapping( INDEX_NAME, IndexType.Customdata );
    }

    private PutMappingResponse doAddMapping( String indexName, IndexType indexType )
    {
        String mapping = mappingProvider.getMapping( indexName, indexType );

        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType.toString() ).source( mapping );

        return this.client.admin().indices().putMapping( mappingRequest ).actionGet();
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

    @PostConstruct
    public void startIndex()
    {
        try
        {
            initalizeIndex( true );
        }
        catch ( Exception e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
