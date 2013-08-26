package com.enonic.cms.web.status.builders;

import org.codehaus.jackson.node.ObjectNode;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public final class ClusterStatusInfoBuilder
    extends StatusInfoBuilder
{
    @Autowired
    private ElasticSearchIndexService elasticSearchIndexService;

    public ClusterStatusInfoBuilder()
    {
        super( "cluster" );
    }

    @Override
    public void build( final ObjectNode json )
    {

        final NodeInfo localNodeInfo = elasticSearchIndexService.getLocalNodeInfo();
        final ClusterStateResponse clusterState = elasticSearchIndexService.getClusterState();

        json.put( "clusterName", clusterState.getClusterName().value() );

        final DiscoveryNodes clusterMembers = clusterState.getState().getNodes();

        final String masterNodeId = clusterMembers.getMasterNodeId();

        final ObjectNode localNodeObject = json.putObject( "localNode" );
        final String localNodeId = localNodeInfo.getNode().getId();
        localNodeObject.put( "id", localNodeId );
        localNodeObject.put( "hostName", localNodeInfo.getHostname() );
        localNodeObject.put( "master", Boolean.toString( masterNodeId.equals( localNodeId ) ) );
        localNodeObject.put( "numberOfNodesSeen", clusterMembers.size() );

        final ObjectNode nodesObject = json.putObject( "members" );
        for ( DiscoveryNode node : clusterMembers )
        {
            final ObjectNode thisNode = nodesObject.putObject( node.getId() );
            buildNodeInfo( masterNodeId, node, thisNode );
        }

    }

    private void buildNodeInfo( final String masterNodeId, final DiscoveryNode node, final ObjectNode thisNode )
    {
        thisNode.put( "name", node.getName() );
        thisNode.put( "id", node.getId() );
        thisNode.put( "address", node.getAddress().toString() );
        thisNode.put( "version", node.getVersion().toString() );
        thisNode.put( "master", Boolean.toString( node.getId().equals( masterNodeId ) ) );
    }

}
