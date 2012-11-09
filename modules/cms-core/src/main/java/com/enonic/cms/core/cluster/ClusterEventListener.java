package com.enonic.cms.core.cluster;

public interface ClusterEventListener
{
    public void handle( ClusterEvent event );
}
