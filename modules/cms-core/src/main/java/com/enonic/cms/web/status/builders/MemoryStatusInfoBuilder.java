package com.enonic.cms.web.status.builders;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public final class MemoryStatusInfoBuilder
    extends StatusInfoBuilder
{
    public MemoryStatusInfoBuilder()
    {
        super( "memory" );
    }

    @Override
    public void build( final ObjectNode json )
    {
    }
}
