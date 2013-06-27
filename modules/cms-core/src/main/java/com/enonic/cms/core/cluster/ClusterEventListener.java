/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.cluster;

public interface ClusterEventListener
{
    public void handle( ClusterEvent event );
}
