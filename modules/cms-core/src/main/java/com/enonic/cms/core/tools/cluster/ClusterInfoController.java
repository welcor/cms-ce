/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.tools.cluster;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.cms.core.tools.AbstractToolController;

public final class ClusterInfoController
    extends AbstractToolController
{
    private Client client;

    public ClusterInfoController()
    {
        setEnterpriseFeature( true );
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String op = req.getParameter( "op" );

        if ( "info".equals( op ) )
        {
            final Map<String, Object> model = Maps.newHashMap();
            model.put( "baseUrl", getBaseUrl( req ) );
            addNodeInfoData( model );
            renderView( req, res, model, "clusterInfoPage_info" );
        }
        else
        {
            final Map<String, Object> model = Maps.newHashMap();
            model.put( "baseUrl", getBaseUrl( req ) );
            renderView( req, res, model, "clusterInfoPage" );
        }
    }

    private void addNodeInfoData( final Map<String, Object> model )
        throws Exception
    {
        final NodesInfoRequestBuilder builder = new NodesInfoRequestBuilder( this.client.admin().cluster() );
        builder.setJvm( true );

        final NodesInfoResponse res = builder.execute().actionGet();
        model.put( "clusterName", res.getClusterName() );
        model.put( "nodeList", toWrapper( res.getNodes() ) );
    }

    private List<ClusterNodeWrapper> toWrapper( final NodeInfo[] list )
    {
        final List<ClusterNodeWrapper> result = Lists.newArrayList();
        for ( final NodeInfo info : list )
        {
            result.add( new ClusterNodeWrapper( info ) );
        }

        return result;
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
