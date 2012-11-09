package com.enonic.cms.core.tools;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHelper;

public class ClusterMonitorController
    extends AbstractToolController
{
    @Override
    protected void doHandleRequest( final HttpServletRequest req, final HttpServletResponse res, final ExtendedMap formItems )
    {
        // TODO: This should be using elastic search instead

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );

        process( req, res, model, "clusterInfoPage" );
    }
}
