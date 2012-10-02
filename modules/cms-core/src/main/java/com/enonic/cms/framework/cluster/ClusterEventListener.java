package com.enonic.cms.framework.cluster;

public interface ClusterEventListener
{
    public void handle( ClusterEvent event );
}
