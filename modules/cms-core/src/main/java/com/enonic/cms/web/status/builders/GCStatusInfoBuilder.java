package com.enonic.cms.web.status.builders;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public final class GCStatusInfoBuilder
    extends StatusInfoBuilder
{
    public GCStatusInfoBuilder()
    {
        super( "gc" );
    }

    @Override
    public void build( final ObjectNode json )
    {
    }
}
