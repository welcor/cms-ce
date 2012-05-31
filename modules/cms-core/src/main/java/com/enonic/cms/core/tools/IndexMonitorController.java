package com.enonic.cms.core.tools;

import java.util.HashMap;
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
import com.enonic.cms.core.search.ContentIndexServiceImpl;
import com.enonic.cms.core.search.ElasticSearchIndexService;
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

    protected static final int DEFAULT_COUNT = 500;

    public static final String getAllQUery = "{\n" +
        "  \"from\" : 0,\n" +
        "  \"size\" : 0,\n" +
        "  \"query\" : {\n" +
        "    \"match_all\" : {\n" +
        "    }\n" +
        "  }\n" +
        "}\n" +
        "";

    protected static enum SortValue
    {
        MaxTime,
        AvgTimeDiff,
        TotalHits,
        AvgTime;
    }

    private ElasticSearchIndexService elasticSearchIndexService;

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {
        SortValue orderBy = getOrderBy( req );

        int count = getCount( req );

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );

        model.put( "numberOfContent", getTotalHitsContent() );
        model.put( "numberOfBinaries", getTotalHitsBinaries() );
        model.put( "numberOfNodes", 1 );

        model.put( "count", count );
        model.put( "orderBy", orderBy );

        process( req, res, model, "indexMonitorPage" );
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
            "      \"key\" : \"" + new Long( contentKey.toString() ).toString() + "\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        return elasticSearchIndexService.search( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content.toString(), termQuery );
    }

    private long getTotalHitsBinaries()
    {
        final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Binaries.toString(), getAllQUery );

        return response.getHits().getTotalHits();
    }

    private long getTotalHitsContent()
    {

        final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Content.toString(), getAllQUery );

        return response.getHits().getTotalHits();
    }

    @Autowired
    public void setElasticSearchIndexService( ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }


}
