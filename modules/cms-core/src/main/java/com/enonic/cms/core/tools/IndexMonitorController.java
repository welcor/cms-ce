package com.enonic.cms.core.tools;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasure;
import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasurer;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 9:23 AM
 */
public class IndexMonitorController
    extends AbstractToolController
{

    protected static enum SortValue
    {
        AvgTimeDiff,
        TotalHits,
        AvgTime;
    }

    private ContentIndexService newContentIndexService;

    private ElasticSearchIndexService elasticSearchIndexService;


    private IndexQueryMeasurer indexQueryMeasurer;

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {

        clearIfFlagged( req );

        SortValue orderBy = getOrderBy( req );

        int count = getCount( req );

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "newIndexNumberOfContent", getTotalHits() );
        model.put( "numberOfNodes", 1 );
        model.put( "indexQueryMeasurerSnapshot", getIndexQueryMeasurerResult( orderBy, count ) );

        process( req, res, model, "indexMonitorPage" );

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
            count = 10;
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


    private String getMapping()
    {

        final Client client = elasticSearchIndexService.getClient();

        ClusterState cs = client.admin().cluster().prepareState().setFilterIndices( "cms" ).execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index( "cms" );
        MappingMetaData mdd = imd.mapping( IndexType.Content.toString() );

        try
        {
            final Map<String, Object> mappingMap = mdd.getSourceAsMap();

            BytesStreamOutput out = new BytesStreamOutput();

            MappingMetaData.writeTo( mdd, out );

            return new String( out.copiedByteArray(), "UTF-8" );

        }
        catch ( IOException e )
        {
            return "";
        }

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
}
