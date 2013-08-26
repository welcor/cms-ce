package com.enonic.cms.web.status.builders;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

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
        final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        build( json.putObject( "heap" ), bean.getHeapMemoryUsage() );
        build( json.putObject( "nonHeap" ), bean.getNonHeapMemoryUsage() );
    }

    private void build( final ObjectNode json, final MemoryUsage mem )
    {
        json.put( "init", mem.getInit() );
        json.put( "max", mem.getMax() );
        json.put( "committed", mem.getCommitted() );
        json.put( "used", mem.getUsed() );
    }
}
