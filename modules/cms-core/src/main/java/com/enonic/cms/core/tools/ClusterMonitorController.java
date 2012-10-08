package com.enonic.cms.core.tools;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.framework.cluster.ClusterManager;

public class ClusterMonitorController
    extends AbstractToolController
{

    private ClusterManager clusterManager;

    @Override
    protected void doHandleRequest( final HttpServletRequest req, final HttpServletResponse res, final ExtendedMap formItems )
    {

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "members", clusterManager.getMembers() );
        model.put( "nodeName", clusterManager.getNodeName() );
        model.put( "isEnabled", "" + clusterManager.isEnabled() );

        process( req, res, model, "clusterInfoPage" );
    }


    @Autowired
    public void setClusterManager( final ClusterManager clusterManager )
    {
        this.clusterManager = clusterManager;
    }
}
