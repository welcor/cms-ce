package com.enonic.cms.web.status.builders;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

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
        long collectionTime = 0;
        long collectionCount = 0;

        final List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        for ( final GarbageCollectorMXBean bean : beans )
        {
            collectionTime += bean.getCollectionTime();
            collectionCount += bean.getCollectionCount();
        }

        json.put( "collectionTime", collectionTime );
        json.put( "collectionCount", collectionCount );
    }
}
