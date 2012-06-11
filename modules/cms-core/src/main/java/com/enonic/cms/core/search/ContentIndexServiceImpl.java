package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import com.enonic.cms.core.content.ContentEntityFetcherImpl;
import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.AggregatedResult;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.IndexValueResultImpl;
import com.enonic.cms.core.content.index.IndexValueResultSet;
import com.enonic.cms.core.content.index.IndexValueResultSetImpl;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetLazyFetcher;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.portal.livetrace.ContentIndexQueryTrace;
import com.enonic.cms.core.portal.livetrace.ContentIndexQueryTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.search.builder.ContentIndexData;
import com.enonic.cms.core.search.builder.ContentIndexDataFactory;
import com.enonic.cms.core.search.query.IndexQueryException;
import com.enonic.cms.core.search.query.IndexValueQueryTranslator;
import com.enonic.cms.core.search.query.QueryTranslator;
import com.enonic.cms.store.dao.ContentDao;

/**
 * This class implements the content index service based on elasticsearch
 */
@Component
public class ContentIndexServiceImpl
    implements ContentIndexService
{
    public final static String CONTENT_INDEX_NAME = "cms";

    public static final int COUNT_THRESHOULD_VALUE = 1000;

    private IndexMappingProvider indexMappingProvider;

    private ElasticSearchIndexService elasticSearchIndexService;

    private final Logger LOG = Logger.getLogger( ContentIndexServiceImpl.class.getName() );

    private final ContentIndexDataFactory contentIndexDataFactory = new ContentIndexDataFactory();

    private QueryTranslator queryTranslator;

    private final IndexValueQueryTranslator indexValueQueryTranslator = new IndexValueQueryTranslator();

    private ContentDao contentDao;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @PostConstruct
    public void initializeContentIndex()
    {
        final boolean indexExists = elasticSearchIndexService.indexExists( CONTENT_INDEX_NAME );

        if ( !indexExists )
        {
            elasticSearchIndexService.createIndex( CONTENT_INDEX_NAME );
            addMapping();
        }
        else
        {
            verifyMapping();
        }
    }

    // TODO: Implement
    private void verifyMapping()
    {

    }

    private void addMapping()
    {
        doAddMapping( IndexType.Content );
        doAddMapping( IndexType.Binaries );
    }

    private void doAddMapping( IndexType indexType )
    {
        String mapping = getMapping( indexType );
        elasticSearchIndexService.putMapping( CONTENT_INDEX_NAME, indexType.toString(), mapping );
    }

    private String getMapping( final IndexType indexType )
    {
        return indexMappingProvider.getMapping( CONTENT_INDEX_NAME, indexType.toString() );
    }

    public void remove( ContentKey contentKey )
    {
        doRemoveEntryWithId( contentKey );
    }

    public void removeByCategory( CategoryKey categoryKey )
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "" );
        contentIndexQuery.setCategoryFilter( Arrays.asList( categoryKey ) );
        doRemoveByQuery( contentIndexQuery );
    }

    public void removeByContentType( ContentTypeKey contentTypeKey )
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "" );
        contentIndexQuery.setContentTypeFilter( Arrays.asList( contentTypeKey ) );
        doRemoveByQuery( contentIndexQuery );
    }

    private void doRemoveByQuery( ContentIndexQuery contentIndexQuery )
    {
        final SearchSourceBuilder build;

        build = queryTranslator.build( contentIndexQuery );

        SearchHits hits = doExecuteSearchRequest( build );

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

    public void index( ContentDocument doc )
    {
        doDeleteExisting( doc, true );
    }


    public void index( ContentDocument doc, boolean deleteExisting )
    {
        doDeleteExisting( doc, deleteExisting );
    }

    private void doDeleteExisting( final ContentDocument doc, final boolean deleteExisting )
    {
        ContentIndexData contentIndexData = contentIndexDataFactory.create( doc );

        if ( deleteExisting )
        {
            doRemoveEntryWithId( doc.getContentKey() );
        }

        elasticSearchIndexService.index( CONTENT_INDEX_NAME, contentIndexData );
    }

    public void indexBulk( List<ContentDocument> docs )
    {

        Set<ContentIndexData> contentIndexDatas = Sets.newHashSet();

        for ( ContentDocument doc : docs )
        {
            ContentIndexData contentIndexData = contentIndexDataFactory.create( doc );

            contentIndexDatas.add( contentIndexData );
        }

        elasticSearchIndexService.index( CONTENT_INDEX_NAME, contentIndexDatas );
    }


    public boolean isIndexed( ContentKey contentKey, final IndexType indexType )
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

            final SearchHits hits = doExecuteSearchRequest( translatedQuerySource );

            LOG.finer( "query: " + translatedQuerySource.toString() + " executed with " + hits.getHits().length + " hits of total " +
                           hits.getTotalHits() );

            final int queryResultTotalSize = new Long( hits.getTotalHits() ).intValue();

            ContentIndexQueryTracer.traceMatchCount( queryResultTotalSize, trace );

            if ( query.getIndex() > queryResultTotalSize )
            {
                final ContentResultSetNonLazy rs = new ContentResultSetNonLazy( query.getIndex() );
                rs.addError( "Index greater than result count: " + query.getIndex() + " greater than " + queryResultTotalSize );
                return rs;
            }

            final int fromIndex = Math.max( query.getIndex(), 0 );

            final ArrayList<ContentKey> keys = new ArrayList<ContentKey>();

            for ( SearchHit hit : hits )
            {
                keys.add( new ContentKey( hit.getId() ) );
            }

            return new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), keys, fromIndex, queryResultTotalSize );
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
    private boolean isFilterBlockingAllContent( ContentIndexQuery query )
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
                // TODO: Could be optimized to not execute query again
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

    public IndexValueResultSet query( IndexValueQuery query )
    {
        final SearchSourceBuilder build;

        try
        {
            build = this.indexValueQueryTranslator.build( query );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Failed to translate query: " + query, e );
        }

        final SearchHits hits = doExecuteSearchRequest( build );

        final IndexValueResultSetImpl resultSet = new IndexValueResultSetImpl( query.getIndex(), Ints.saturatedCast( hits.totalHits() ) );

        for ( SearchHit hit : hits )
        {
            resultSet.add( createIndexValueResult( hit ) );
        }

        LOG.finer( "query: " + build.toString() + " executed with " + resultSet.getCount() + " hits of total " +
                       resultSet.getTotalCount() );

        return resultSet;
    }

    private IndexValueResultImpl createIndexValueResult( SearchHit hit )
    {
        final Map<String, SearchHitField> fields = hit.getFields();

        if ( fields.size() != 1 )
        {
            throw new ContentIndexException( "Expected one field hit for query, found " + fields.size() );
        }

        ContentKey contentKey = new ContentKey( hit.getId() );

        final Map.Entry<String, SearchHitField> next = fields.entrySet().iterator().next();

        final String value = (String) next.getValue().getValue();

        return new IndexValueResultImpl( contentKey, value );
    }


    private SearchHits doExecuteSearchRequest( SearchSourceBuilder searchSourceBuilder )
    {
        final SearchResponse res =
            elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Content.toString(), searchSourceBuilder );

        return res.getHits();
    }

    public SearchResponse query( String query )
    {
        return elasticSearchIndexService.search( CONTENT_INDEX_NAME, IndexType.Content.toString(), query );
    }

    public void flush()
    {
        elasticSearchIndexService.flush( CONTENT_INDEX_NAME );
    }

    // TODO: We dont implement this one yet
    public AggregatedResult query( AggregatedQuery query )
    {
        throw new RuntimeException(
            "Method not implemented in class " + this.getClass().getName() + ": " + "query( AggregatedQuery query )" );
        //return null;
    }

    @Override
    public void initializeMapping()
    {
        elasticSearchIndexService.deleteMapping( CONTENT_INDEX_NAME, IndexType.Content );
        elasticSearchIndexService.deleteMapping( CONTENT_INDEX_NAME, IndexType.Binaries );
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
    public Collection<ContentIndexEntity> getContentIndexedFields( ContentKey contentKey )
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