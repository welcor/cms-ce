/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.cluster;

public final class NopClusterEventPublisher
    implements ClusterEventPublisher
{
    @Override
    public void publish( final ClusterEvent event )
    {
        // Do nothing
    }
}
