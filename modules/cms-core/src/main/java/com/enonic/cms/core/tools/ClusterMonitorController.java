package com.enonic.cms.core.tools;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ClusterMonitorController
    extends AbstractToolController
{
    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        // TODO: This should be using elastic search instead

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", getBaseUrl( req ) );

        renderView( req, res, model, "clusterInfoPage" );
    }
}
