package com.enonic.cms.framework.cache;

import com.enonic.cms.core.cluster.ClusterEvent;
import com.enonic.cms.core.cluster.ClusterEventPublisher;

public final class NopClusterEventPublisher
    implements ClusterEventPublisher
{
    @Override
    public void publish( final ClusterEvent event )
    {
        // Do nothing
    }
}
