package com.enonic.cms.core.search;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.*;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetLazyFetcher;
import com.enonic.cms.core.search.builder.ContentIndexDataBuilder;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.search.query.IndexQueryException;
import com.enonic.cms.core.search.query.QueryTranslator;
import com.enonic.cms.store.dao.ContentDao;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:41 PM
 */
public class ContentIndexServiceImpl
        implements ContentIndexService {
    public final static String INDEX_NAME = "cms";

    private IndexMappingProvider mappingProvider;

    private Client client;

    private IndexRequestCreator indexRequestCreator;

    @Autowired
    private ContentIndexDataBuilder indexDataBuilder;

    @Autowired
    private QueryTranslator translator;

    @Autowired
    private ContentDao contentDao;

    public void createIndex() {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);

        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.loadFromSource(IndexAnalyzerSettingsBuilder.buildAnalyserSettings());

        //TODO: Other settings

        createIndexRequest.settings(settings);
        client.admin().indices().create(createIndexRequest).actionGet();

        addMapping();
    }

    public void addMapping() {
        doAddMapping(INDEX_NAME, IndexType.Content);
        doAddMapping(INDEX_NAME, IndexType.Binaries);
        doAddMapping(INDEX_NAME, IndexType.Customdata);
    }

    private PutMappingResponse doAddMapping(String indexName, IndexType indexType) {
        String mapping = mappingProvider.getMapping(indexName, indexType);

        PutMappingRequest mappingRequest = new PutMappingRequest(indexName).type(indexType.toString()).source(mapping);

        return this.client.admin().indices().putMapping(mappingRequest).actionGet();
    }

    public int remove(ContentKey contentKey) {

        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeByCategory(CategoryKey categoryKey) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeByContentType(ContentTypeKey contentTypeKey) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void index(ContentDocument doc, boolean deleteExisting) {
        ContentIndexData contentIndexData = indexDataBuilder.build(doc, ContentIndexDataBuilderSpecification.createBuildAllConfig());

        Set<IndexRequest> indexRequests = indexRequestCreator.createIndexRequests(contentIndexData);

        for (IndexRequest indexRequest : indexRequests) {
            doIndex(indexRequest);
        }
    }

    private void doIndex(IndexRequest request) {
        this.client.index(request).actionGet();
    }


    public boolean isIndexed(ContentKey contentKey) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // TODO: We dont implement this one yet
    public IndexValueResultSet query(IndexValueQuery query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // TODO: We dont implement this one yet
    public AggregatedResult query(AggregatedQuery query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Autowired
    public void setMappingProvider(IndexMappingProvider mappingProvider) {
        this.mappingProvider = mappingProvider;
    }

    @Autowired
    public void setClient(Client client) {
        this.client = client;
    }

    @PostConstruct
    public void startIndex() {

        indexRequestCreator = new IndexRequestCreator(INDEX_NAME);

        try {
            initalizeIndex(false);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void initalizeIndex(boolean force)
            throws Exception {

        final boolean indexExists = indexExists();

        if (indexExists && !force) {
            return;
        } else if (indexExists) {
            deleteIndex();
        }

        createIndex();
        addMapping();
    }

    private   DeleteIndexResponse deleteIndex()
            throws Exception {
        return this.client.admin().indices().delete(new DeleteIndexRequest(INDEX_NAME)).actionGet();
    }

    public boolean indexExists() {
        try {
            this.client.admin().indices().status(new IndicesStatusRequest(INDEX_NAME)).actionGet();
            return true;
        } catch (ElasticSearchException e) {
            return false;
        }
    }

//    public void index( final ContentIndexData contentIndexData )
//    {
//        Set<IndexRequest> indexRequests = indexRequestCreator.createIndexRequests( contentIndexData );
//
//        for ( IndexRequest indexRequest : indexRequests )
//        {
//            doIndex( indexRequest );
//        }
//    }
//
//    public void index( final List<ContentIndexData> contentIndexDatas )
//    {
//        doBulkIndex( contentIndexDatas );
//    }
//
//    private void doBulkIndex( List<ContentIndexData> contentIndexDatas )
//    {
//        BulkRequestBuilder bulkRequest = client.prepareBulk();
//
//        for ( ContentIndexData contentIndexData : contentIndexDatas )
//        {
//            addIndexRequests( bulkRequest, indexRequestCreator.createIndexRequests( contentIndexData ) );
//        }
//
//        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
//
//        if ( bulkResponse.hasFailures() )
//        {
//            //TODO: Handle exception
//        }
//    }
//
//

//
//    private void addIndexRequests( BulkRequestBuilder bulkRequest, Set<IndexRequest> requests )
//    {
//        for ( IndexRequest request : requests )
//        {
//            bulkRequest.add( request );
//        }
//    }
//
//
//    public void delete( final ContentKey... contentKeys )
//    {
//
//        for ( ContentKey contentKey : contentKeys )
//        {
//            doDelete( INDEX_NAME, contentKey.toString(), IndexType.Customdata.toString(), IndexType.Binaries.toString(),
//                      IndexType.Content.toString() );
//        }
//    }
//
//    private void doDelete( final String indexName, final String id, final String... indexTypes )
//    {
//        // TODO: create query with _parent or _id
//        final DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest( indexName ).types( indexTypes ).query( "" );
//        DeleteByQueryResponse resp = this.client.deleteByQuery( deleteByQueryRequest ).actionGet();
//    }
//
//    public void delete( CategoryKey... categoryKeys )
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    public void delete( ContentTypeKey... contentTypeKeys )
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    public void update( ContentKey... contentKeys )
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    public void update( CategoryKey... categoryKeys )
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    public void update( ContentTypeKey... contentTypeKeys )
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//

    public ContentResultSet query(ContentIndexQuery query) {

        final SearchSourceBuilder build;
        try {
            build = this.translator.build(query);
        } catch (Exception e) {
            throw new IndexQueryException("Failed to translate query: " + query.getQuery(), e);
        }

        final SearchRequest req = Requests.searchRequest(INDEX_NAME).types(IndexType.Content.toString()).source(build);

        final SearchResponse res = this.client.search(req).actionGet();
        final SearchHits hits = res.getHits();

        List<ContentKey> resultKeys = new ArrayList<ContentKey>();

        for (final SearchHit hit : hits) {
            resultKeys.add(new ContentKey(hit.getId()));
        }

        //TODO: This should get the correct from and to-index
        return new ContentResultSetLazyFetcher(new ContentEntityFetcherImpl(contentDao), resultKeys, 0, (int) hits.getTotalHits());

        /*
        final ContentSearchHits result = new ContentSearchHits( query.getFrom(), (int) hits.getTotalHits() );
        for ( final SearchHit hit : hits )
        {
            result.add( new ContentKey( hit.getId() ), hit.score() );
        }

        return result;
        */
    }

}
