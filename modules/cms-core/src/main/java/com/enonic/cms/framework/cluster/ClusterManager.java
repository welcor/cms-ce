package com.enonic.cms.framework.cluster;

import java.util.List;

public interface ClusterManager
{
    public String getNodeName();

    public List<String> getMembers();

    public void addListener( ClusterEventListener listener );

    public void removeListener( ClusterEventListener listener );

    public void publish( ClusterEvent event );

    public boolean isEnabled();
}
