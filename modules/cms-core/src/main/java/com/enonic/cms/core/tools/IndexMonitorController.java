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
import com.enonic.cms.core.search.IndexPerformance.IndexQueryMeasure;
import com.enonic.cms.core.search.IndexPerformance.IndexQueryMeasurer;
import com.enonic.cms.core.search.IndexType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 9:23 AM
 */
public class IndexMonitorController
    extends AbstractToolController
{

    protected static final String AVG_TIME_DIFF = "avgTimeDiff";

    protected static final String TOTAL_HITS = "totalHits";

    private ContentIndexService newContentIndexService;

    private ElasticSearchIndexService elasticSearchIndexService;


    private IndexQueryMeasurer indexQueryMeasurer;

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {

        String clear = req.getParameter( "clear" );

        if ( StringUtils.isNotBlank( clear ) )
        {
            indexQueryMeasurer.clearStatistics();
        }

        String orderBy = req.getParameter( "orderby" );

        if ( StringUtils.isBlank( orderBy ) )
        {
            orderBy = AVG_TIME_DIFF;
        }

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

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "newIndexNumberOfContent", getTotalHits() );
        model.put( "numberOfNodes", 1 );
        // model.put( "contentMapping", getMapping() );

        model.put( "indexQueryMeasurerSnapshot", getIndexQueryMeasurerResult( orderBy, count ) );

        /*
        final ExtensionSet extensions = this.pluginManager.getExtensions();
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "functionLibraryExtensions", toWrappers( extensions.getAllFunctionLibraries() ) );
        model.put( "autoLoginExtensions", toWrappers( extensions.getAllHttpAutoLoginPlugins() ) );
        model.put( "httpInterceptors", toWrappers( extensions.getAllHttpInterceptors() ) );
        model.put( "httpResponseFilters", toWrappers( extensions.getAllHttpResponseFilters() ) );
        model.put( "taskExtensions", toWrappers( extensions.getAllTaskPlugins() ) );
        model.put( "textExtractorExtensions", toWrappers( extensions.getAllTextExtractorPlugins() ) );
        model.put( "pluginHandles", toPluginWrappers( this.pluginManager.getPlugins() ) );
        */

        process( req, res, model, "indexMonitorPage" );

    }

    private Collection<IndexQueryMeasure> getIndexQueryMeasurerResult( String orderBy, Integer count )
    {
        if ( AVG_TIME_DIFF.equals( orderBy ) )
        {
            return indexQueryMeasurer.getMeasuresOrderedByAvgDiffTime( count );
        }

        if ( TOTAL_HITS.equals( orderBy ) )
        {
            return indexQueryMeasurer.getMeasuresOrderedByTotalExecutions(count);
        }

        return indexQueryMeasurer.getMeasuresOrderedByAvgDiffTime( count );
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
