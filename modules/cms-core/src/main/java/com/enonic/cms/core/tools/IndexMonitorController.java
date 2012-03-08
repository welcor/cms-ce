package com.enonic.cms.core.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.search.ContentIndexServiceImpl;
import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasure;
import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasurer;
import com.enonic.cms.core.search.querymeasurer.QueryDiffEntry;
import com.enonic.cms.core.search.querymeasurer.QueryResultComparer;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 9:23 AM
 */
public class IndexMonitorController
    extends AbstractToolController
{

    protected static final int DEFAULT_COUNT = 500;

    protected static enum SortValue
    {
        MaxTime,
        AvgTimeDiff,
        TotalHits,
        AvgTime;
    }

    private ContentIndexService newContentIndexService;

    private ElasticSearchIndexService elasticSearchIndexService;


    private IndexQueryMeasurer indexQueryMeasurer;

    private QueryResultComparer queryResultComparer;

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {

        final String systemInfo = req.getParameter( "measuresList" );
        final String diffList = req.getParameter( "diffList" );
        final String queryContent = req.getParameter( "queryContent" );

        clearIfFlagged( req );

        SortValue orderBy = getOrderBy( req );

        int count = getCount( req );

        if ( StringUtils.isNotBlank( queryContent ) )
        {
            final HashMap<String, Object> model = new HashMap<String, Object>();

            model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
            model.put( "contentFields", getContentFields( req ) );
            process( req, res, model, "indexMonitorContentQueryWindow" );

        }
        else if ( StringUtils.isNotBlank( systemInfo ) )
        {
            final HashMap<String, Object> model = new HashMap<String, Object>();

            model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
            model.put( "indexQueryMeasurerSnapshot", getIndexQueryMeasurerResult( orderBy, count ) );
            model.put( "totalHitsOnIndex", indexQueryMeasurer.getTotalQueriesOnIndex() );
            model.put( "numberOfRecoredQueries", indexQueryMeasurer.getRecordedQueries() );

            process( req, res, model, "indexMonitorMeasureList" );
        }

        else if ( StringUtils.isNotBlank( diffList ) )
        {
            final HashMap<String, Object> model = new HashMap<String, Object>();

            model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
            model.put( "queryResultDiffList", createQueryResultDiffList() );
            process( req, res, model, "indexMonitorDiffList" );
        }

        else
        {
            final HashMap<String, Object> model = new HashMap<String, Object>();

            model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
            model.put( "totalHitsOnIndex", indexQueryMeasurer.getTotalQueriesOnIndex() );
            model.put( "numberOfRecoredQueries", indexQueryMeasurer.getRecordedQueries() );
            model.put( "newIndexNumberOfContent", getTotalHits() );
            model.put( "numberOfNodes", 1 );
            model.put( "indexQueryMeasurerSnapshot", getIndexQueryMeasurerResult( orderBy, count ) );
            model.put( "count", count );
            model.put( "orderBy", orderBy );

            process( req, res, model, "indexMonitorPage" );
        }
    }

    private Map<String, String> getContentFields( final HttpServletRequest req )
    {
        final String contentKey = req.getParameter( "contentKey" );

        final Map<String, String> resultMap = Maps.newTreeMap();

        final Map<String, SearchHitField> fieldMapForId = getFieldMapForId( new ContentKey( contentKey ) );

        for ( String fieldName : fieldMapForId.keySet() )
        {
            final SearchHitField searchHitField = fieldMapForId.get( fieldName );
            resultMap.put( searchHitField.getName(), searchHitField.getValue().toString() );
        }

        return resultMap;
    }

    private List<QueryDiffEntry> createQueryResultDiffList()
    {

        return queryResultComparer.getQueryDiffEntries();

    }

    private SortValue getOrderBy( HttpServletRequest req )
    {
        String orderByStringValue = req.getParameter( "orderby" );

        if ( StringUtils.isBlank( orderByStringValue ) )
        {
            return SortValue.AvgTimeDiff;
        }

        SortValue orderBy = SortValue.valueOf( orderByStringValue );

        if ( orderBy == null )
        {
            return SortValue.AvgTimeDiff;
        }

        return orderBy;
    }

    private int getCount( HttpServletRequest req )
    {
        String countString = req.getParameter( "count" );

        int count;

        if ( !StringUtils.isNumeric( countString ) )
        {
            count = DEFAULT_COUNT;
        }
        else
        {
            count = new Integer( countString );
        }
        return count;
    }

    private void clearIfFlagged( HttpServletRequest req )
    {
        String clear = req.getParameter( "clear" );

        if ( StringUtils.isNotBlank( clear ) )
        {
            indexQueryMeasurer.clearStatistics();
        }
    }

    private Collection<IndexQueryMeasure> getIndexQueryMeasurerResult( SortValue orderBy, Integer count )
    {
        switch ( orderBy )
        {
            case MaxTime:
                return indexQueryMeasurer.getMeasuresOrderedByMaxTime( count );
            case AvgTime:
                return indexQueryMeasurer.getMeasuresOrderedByAvgTime( count );
            case AvgTimeDiff:
                return indexQueryMeasurer.getMeasuresOrderedByAvgDiffTime( count );
            case TotalHits:
                return indexQueryMeasurer.getMeasuresOrderedByTotalExecutions( count );
            default:
                return indexQueryMeasurer.getMeasuresOrderedByAvgDiffTime( count );
        }
    }


    protected Map<String, SearchHitField> getFieldMapForId( ContentKey contentKey )
    {
        SearchResponse result = fetchDocumentByContentKey( contentKey );

        SearchHit hit = result.getHits().getAt( 0 );

        return hit.getFields();
    }

    private SearchResponse fetchDocumentByContentKey( ContentKey contentKey )
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + 100 + ",\n" +
            "\"fields\" : [\"*\"],\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"key_numeric\" : \"" + new Long( contentKey.toString() ).toString() + "\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        return elasticSearchIndexService.search( ContentIndexServiceImpl.INDEX_NAME, IndexType.Content, termQuery );
    }


    private long getTotalHits()
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 0,\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "";

        final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Content, termQuery );

        return response.getHits().getTotalHits();
    }


    public void setNewContentIndexService( ContentIndexService newContentIndexService )
    {
        this.newContentIndexService = newContentIndexService;
    }

    @Autowired
    public void setElasticSearchIndexService( ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }

    @Autowired
    public void setIndexQueryMeasurer( IndexQueryMeasurer indexQueryMeasurer )
    {
        this.indexQueryMeasurer = indexQueryMeasurer;
    }

    @Autowired
    public void setQueryResultComparer( final QueryResultComparer queryResultComparer )
    {
        this.queryResultComparer = queryResultComparer;
    }
}
