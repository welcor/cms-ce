package com.enonic.cms.core.cluster;

public interface ClusterEventPublisher
{
    public void publish( ClusterEvent event );
}
