/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Oct 6, 2010
 */
public class CompletedPortalRequests
{
    private static long historyCounter = 0;

    private int maxSize;

    private final LinkedList<PortalRequestTrace> list = new LinkedList<PortalRequestTrace>();

    public CompletedPortalRequests( int maxSize )
    {
        this.maxSize = maxSize;
    }

    public void add( PortalRequestTrace portalRequestTrace )
    {
        synchronized ( list )
        {
            portalRequestTrace.setCompletedNumber( ++historyCounter );
            list.addFirst( portalRequestTrace );
            doRetainSize();
        }
    }

    public List<PortalRequestTrace> getList()
    {
        synchronized ( list )
        {
            return ImmutableList.copyOf( list );
        }
    }

    public List<PortalRequestTrace> getCompletedAfter( long completedNumber )
    {
        LinkedList<PortalRequestTrace> list = new LinkedList<PortalRequestTrace>();
        for ( PortalRequestTrace trace : getList() )
        {
            if ( trace.getCompletedNumber() > completedNumber )
            {
                list.addLast( trace );
            }
        }
        return list;
    }

    public List<PortalRequestTrace> getCompletedBefore( long completedNumber )
    {
        LinkedList<PortalRequestTrace> list = new LinkedList<PortalRequestTrace>();
        for ( PortalRequestTrace trace : getList() )
        {
            if ( trace.getCompletedNumber() < completedNumber )
            {
                list.addLast( trace );
            }
        }
        return list;
    }

    private void doRetainSize()
    {
        if ( list.size() > maxSize )
        {
            list.removeLast();
        }
    }

    public int getSize()
    {
        return list.size();
    }
}
