package com.enonic.cms.core.tools.index;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.tools.AbstractToolController;

public class IndexSettingsToolController
    extends AbstractToolController
{

    private ElasticSearchIndexService elasticSearchIndexService;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", getBaseUrl( req ) );
        model.put( "indexSettings", getIndexSetting() );
        renderView( req, res, model, "indexSettingsPage" );
    }


    private Map<String, String> getIndexSetting()
    {
        return elasticSearchIndexService.getIndexSettings( "cms" );
    }

    @Override
    protected void doPost( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {

        final HashMap<String, Object> model = new HashMap<String, Object>();

        if ( req.getParameter( "updateSetting" ) != null )
        {
            if ( req.getParameter( "value" ) != null )
            {
                elasticSearchIndexService.updateIndexSetting( "cms", req.getParameter( "updateSetting" ), req.getParameter( "value" ) );
            }

            redirectToReferrer( req, res );
        }

        model.put( "baseUrl", getBaseUrl( req ) );
        model.put( "indexSettings", getIndexSetting() );

        renderView( req, res, model, "indexSettingsPage" );
    }


    @Autowired
    public void setElasticSearchIndexService( final ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }
}
