package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.common.primitives.Ints;

import com.enonic.cms.core.content.ContentEntityFetcherImpl;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetLazyFetcher;
import com.enonic.cms.core.portal.livetrace.ContentIndexQueryTrace;
import com.enonic.cms.core.portal.livetrace.ContentIndexQueryTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.search.builder.ContentIndexData;
import com.enonic.cms.core.search.builder.ContentIndexDataFactory;
import com.enonic.cms.core.search.query.AggregatedQuery;
import com.enonic.cms.core.search.query.AggregatedQueryTranslator;
import com.enonic.cms.core.search.query.AggregatedResult;
import com.enonic.cms.core.search.query.AggregatedResultImpl;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.core.search.query.IndexQueryException;
import com.enonic.cms.core.search.query.IndexValueQuery;
import com.enonic.cms.core.search.query.IndexValueQueryTranslator;
import com.enonic.cms.core.search.query.IndexValueResultImpl;
import com.enonic.cms.core.search.query.IndexValueResultSet;
import com.enonic.cms.core.search.query.IndexValueResultSetImpl;
import com.enonic.cms.core.search.query.QueryField;
import com.enonic.cms.core.search.query.QueryFieldFactory;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;
import com.enonic.cms.core.search.query.QueryTranslator;
import com.enonic.cms.core.search.result.FacetsResultSet;
import com.enonic.cms.core.search.result.FacetsResultSetCreator;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentDao;

/**
 * This class implements the content index service based on elasticsearch
 */
@Component
public class ContentIndexServiceImpl
    implements ContentIndexService
{
    public final static String CONTENT_INDEX_NAME = "cms";

    private static final int COUNT_THRESHOULD_VALUE = 1000;

    private IndexMappingProvider indexMappingProvider;

    private ElasticSearchIndexService elasticSearchIndexService;

    private final Logger LOG = Logger.getLogger( ContentIndexServiceImpl.class.getName() );

    private final ContentIndexDataFactory contentIndexDataFactory = new ContentIndexDataFactory();

    private QueryTranslator queryTranslator;

    private final IndexValueQueryTranslator indexValueQueryTranslator = new IndexValueQueryTranslator();

    private final AggregatedQueryTranslator aggregatedQueryTranslator = new AggregatedQueryTranslator();

    private final FacetsResultSetCreator facetsResultSetCreator = new FacetsResultSetCreator();

    private ContentDao contentDao;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @Autowired
    private TimeService timeService;

    @PostConstruct
    public void initializeContentIndex()
    {
        final ClusterHealthResponse clusterHealth = elasticSearchIndexService.getClusterHealth( CONTENT_INDEX_NAME, true );

        LOG.info( "Cluster in state: " + clusterHealth.status().toString() );

        final boolean indexExists = elasticSearchIndexService.indexExists( CONTENT_INDEX_NAME );

        if ( !indexExists )
        {
            try
            {
                elasticSearchIndexService.createIndex( CONTENT_INDEX_NAME );
            }
            catch ( org.elasticsearch.indices.IndexAlreadyExistsException e )
            {
                LOG.warning( "Tried to create index, but index already exists, skipping" );
            }
            addMapping();
        }
    }

    private void addMapping()
    {
        doAddMapping( IndexType.Content );
        doAddMapping( IndexType.Binaries );
    }

    private void doAddMapping( final IndexType indexType )
    {
        String mapping = getMapping( indexType );
        elasticSearchIndexService.putMapping( CONTENT_INDEX_NAME, indexType.toString(), mapping );
    }

    private String getMapping( final IndexType indexType )
    {
        return indexMappingProvider.getMapping( CONTENT_INDEX_NAME, indexType.toString() );
    }

    public void remove( final ContentKey contentKey )
    {
        doRemoveEntryWithId( contentKey );
    }

    public void removeByCategory( final CategoryKey categoryKey )
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "" );
        contentIndexQuery.setCategoryFilter( Arrays.asList( categoryKey ) );
        doRemoveByQuery( contentIndexQuery );
    }

    public void removeByContentType( final ContentTypeKey contentTypeKey )
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "" );
        contentIndexQuery.setContentTypeFilter( Arrays.asList( contentTypeKey ) );
        doRemoveByQuery( contentIndexQuery );
    }

    private void doRemoveByQuery( final ContentIndexQuery contentIndexQuery )
    {
        final SearchSourceBuilder build;

        build = queryTranslator.build( contentIndexQuery );

        SearchResponse searchResponse = doExecuteSearchRequest( build );
        SearchHits hits = searchResponse.getHits();

        final int entriesToDelete = hits.getHits().length;

        LOG.fine( "Prepare to delete: " + entriesToDelete + " entries from index " + CONTENT_INDEX_NAME );

        for ( SearchHit hit : hits )
        {
            final ContentKey contentKey = new ContentKey( hit.getId() );
            doRemoveEntryWithId( contentKey );
        }

        LOG.fine( "Deleted from index " + CONTENT_INDEX_NAME + ", " + entriesToDelete + " entries successfully" );
    }

    private void doRemoveEntryWithId( final ContentKey contentKey )
    {
        elasticSearchIndexService.delete( CONTENT_INDEX_NAME, IndexType.Binaries, contentKey );
        elasticSearchIndexService.delete( CONTENT_INDEX_NAME, IndexType.Content, contentKey );
    }

    public void index( final ContentDocument doc )
    {
        doIndex( doc, false );
    }


    public void index( final ContentDocument doc, final boolean updateMetadataOnly )
    {
        doIndex( doc, updateMetadataOnly );
    }

    private void doIndex( final ContentDocument doc, final boolean updateMetadataOnly )
    {
        ContentIndexData contentIndexData = contentIndexDataFactory.create( doc, updateMetadataOnly );

        if ( !updateMetadataOnly )
        {
            doRemoveEntryWithId( doc.getContentKey() );
        }

        elasticSearchIndexService.index( CONTENT_INDEX_NAME, contentIndexData );
    }

    public boolean isIndexed( final ContentKey contentKey, final IndexType indexType )
    {
        return elasticSearchIndexService.get( CONTENT_INDEX_NAME, indexType, contentKey );
    }

    public void optimize()
    {
        elasticSearchIndexService.optimize( CONTENT_INDEX_NAME );
    }

    public ContentResultSet query( final ContentIndexQuery query )
    {
        final SearchSourceBuilder translatedQuerySource;

        if ( isFilterBlockingAllContent( query ) )
        {
            return new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), new LinkedList<ContentKey>(), 0, 0 );
        }

        final ContentIndexQueryTrace trace = ContentIndexQueryTracer.startTracing( livePortalTraceService );

        try
        {
            optimizeCount( query );

            translatedQuerySource = buildQuerySource( query );

            ContentIndexQueryTracer.traceQuery( query, query.getIndex(), query.getCount(), translatedQuerySource.toString(), trace );

            ContentIndexQueryTracer.traceElasticSearchStartTime( trace, timeService );
            final SearchResponse searchResponse = doExecuteSearchRequest( translatedQuerySource );
            ContentIndexQueryTracer.traceElasticSearchFinishedTime( trace, timeService );

            SearchHits searchHits = searchResponse.getHits();

            LOG.finer(
                "query: " + translatedQuerySource.toString() + " executed with " + searchHits.getHits().length + " searchHits of total " +
                    searchHits.getTotalHits() );

            final int queryResultTotalSize = new Long( searchHits.getTotalHits() ).intValue();

            ContentIndexQueryTracer.traceMatchCount( queryResultTotalSize, trace );

            final int fromIndex = Math.max( query.getIndex(), 0 );

            final ArrayList<ContentKey> keys = new ArrayList<ContentKey>();

            for ( final SearchHit hit : searchHits )
            {
                keys.add( new ContentKey( hit.getId() ) );
            }

            final ContentResultSetLazyFetcher contentResult =
                new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), keys, fromIndex, queryResultTotalSize );

            final FacetsResultSet facetsResultSet = facetsResultSetCreator.createResultSet( searchResponse );
            contentResult.setFacetsResultSet( facetsResultSet );

            return contentResult;
        }
        finally
        {
            ContentIndexQueryTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * Check the filters to see if they may be set so that everything is filtered out. This happens if the filters are not <code>null</code>
     * so that they are applied, but does not contain any elements. If so, there's no point in running the query to the database, as all
     * results will be filtered out anyway.
     *
     * @param query The query, containing all the filters.
     * @return <code>true</code> if the filter does have openings. <code>false</code> if the filters are set so that no result will be let
     *         through the filter, and running the query is superfluous.
     */
    private boolean isFilterBlockingAllContent( final ContentIndexQuery query )
    {
        final boolean isCategoryFilterBlocked = ( ( query.getCategoryFilter() != null ) && ( query.getCategoryFilter().size() == 0 ) );
        final boolean isContentFilterBlocked = ( ( query.getContentFilter() != null ) && ( query.getContentFilter().size() == 0 ) );
        final boolean isContentTypeFilterBlocked =
            ( ( query.getContentTypeFilter() != null ) && ( query.getContentTypeFilter().size() == 0 ) );
        final boolean isSectionFilterBlocked = ( ( query.getSectionFilter() != null ) && ( query.getSectionFilter().size() == 0 ) );

        return isCategoryFilterBlocked || isContentFilterBlocked || isContentTypeFilterBlocked || isSectionFilterBlocked;
    }

    private void optimizeCount( final ContentIndexQuery query )
    {
        if ( query.getCount() >= COUNT_THRESHOULD_VALUE )
        {
            final int actualNumberOfHits = getActualNumberOfHits( query );

            if ( actualNumberOfHits < query.getCount() )
            {
                query.setCount( actualNumberOfHits == 0 ? 1 : actualNumberOfHits );
            }
        }
    }

    private SearchSourceBuilder buildQuerySource( final ContentIndexQuery query )
    {
        return this.queryTranslator.build( query );
    }

    private int getActualNumberOfHits( final ContentIndexQuery query )
    {
        final SearchSourceBuilder searchSource = queryTranslator.build( query, 1 );

        final long actualCount = elasticSearchIndexService.count( CONTENT_INDEX_NAME, IndexType.Content.toString(), searchSource );

        return Ints.saturatedCast( actualCount );
    }

    public IndexValueResultSet query( final IndexValueQuery query )
    {
        final SearchSourceBuilder build;

        final String path = QueryFieldNameResolver.resolveQueryFieldName( query.getField() );
        final QueryField queryField = QueryFieldFactory.resolveQueryField( path );

        try
        {
            build = this.indexValueQueryTranslator.build( query, queryField );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Failed to translate query: " + query, e );
        }

        final SearchResponse searchResponse = doExecuteSearchRequest( build );
        final SearchHits hits = searchResponse.getHits();

        final IndexValueResultSetImpl resultSet = new IndexValueResultSetImpl( query.getIndex(), Ints.saturatedCast( hits.totalHits() ) );

        for ( SearchHit hit : hits )
        {
            resultSet.add( createIndexValueResult( hit, queryField ) );
        }

        LOG.finer( "query: " + build.toString() + " executed with " + resultSet.getCount() + " hits of total " +
                       resultSet.getTotalCount() );

        return resultSet;
    }


    private IndexValueResultImpl createIndexValueResult( final SearchHit hit, final QueryField queryField )
    {
        Assert.notNull( hit.getSource(), "Source is empty from search result" );

        final Map<String, Object> fields = hit.getSource();

        ContentKey contentKey = new ContentKey( hit.getId() );

        final ArrayList<String> fieldValue = (ArrayList<String>) fields.get( queryField.getFieldName() );

        return new IndexValueResultImpl( contentKey, fieldValue.get( 0 ) );
    }

    private SearchResponse doExecuteSearchRequest( final SearchSourceBuilder searchSourceBuilder )
    {
        final SearchResponse searchResponse =
            elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Content.toString(), searchSourceBuilder );

        return searchResponse;
    }

    public SearchResponse query( final String query )
    {
        return elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Content.toString(), query );
    }

    public void flush()
    {
        elasticSearchIndexService.flush( CONTENT_INDEX_NAME );
    }

    public AggregatedResult query( final AggregatedQuery query )
    {
        final SearchSourceBuilder builder;

        try
        {
            builder = this.aggregatedQueryTranslator.build( query );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Failed to translate aggregated query: " + query, e );
        }

        final SearchResponse response = elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Content.toString(), builder );

        final StatisticalFacet statisticalFacet =
            FacetExtractor.getStatisticalFacet( response, AggregatedQueryTranslator.AGGREGATED_FACET_NAME );

        return new AggregatedResultImpl( statisticalFacet.count(), statisticalFacet.min(), statisticalFacet.max(), statisticalFacet.total(),
                                         statisticalFacet.getMean() );
    }

    @Override
    public void reinitializeIndex()
    {
        elasticSearchIndexService.deleteMapping( CONTENT_INDEX_NAME, IndexType.Content );
        elasticSearchIndexService.deleteMapping( CONTENT_INDEX_NAME, IndexType.Binaries );
        addMapping();
    }

    @Override
    public boolean indexExists()
    {
        elasticSearchIndexService.getClusterHealth( CONTENT_INDEX_NAME, true );

        return elasticSearchIndexService.indexExists( CONTENT_INDEX_NAME );
    }

    @Override
    public void createIndex()
    {
        elasticSearchIndexService.createIndex( CONTENT_INDEX_NAME );
        addMapping();
    }

    @Autowired
    public void setIndexMappingProvider( IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setQueryTranslator( QueryTranslator queryTranslator )
    {
        this.queryTranslator = queryTranslator;
    }

    @Autowired
    public void setElasticSearchIndexService( ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }

    @Override
    public Collection<ContentIndexedFields> getContentIndexedFields( ContentKey contentKey )
    {
        final Map<String, GetField> contentFields = elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Content, contentKey );
        final Map<String, GetField> binaryFields = elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Binaries, contentKey );

        // merge content and binary fields, overwrite using content value if same field name exists in both
        final Map<String, GetField> fields = new HashMap<String, GetField>( binaryFields );
        fields.putAll( contentFields );

        final ElasticSearchIndexedFieldsTranslator indexFieldsTranslator = new ElasticSearchIndexedFieldsTranslator();
        return indexFieldsTranslator.generateContentIndexFieldSet( contentKey, fields );
    }


}
