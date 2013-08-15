package com.enonic.cms.web.status.builders;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public final class ClusterStatusInfoBuilder
    extends StatusInfoBuilder
{
    public ClusterStatusInfoBuilder()
    {
        super( "cluster" );
    }

    @Override
    public void build( final ObjectNode json )
    {
    }
}
