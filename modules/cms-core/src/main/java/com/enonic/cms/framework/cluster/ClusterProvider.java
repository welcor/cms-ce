package com.enonic.cms.framework.cluster;

import java.util.List;

public interface ClusterProvider
{
    public String getNodeName();

    public List<String> getMembers();

    public void publish( ClusterEvent event );

    public void start( ClusterEventListener listener )
        throws Exception;

    public void stop()
        throws Exception;
}
