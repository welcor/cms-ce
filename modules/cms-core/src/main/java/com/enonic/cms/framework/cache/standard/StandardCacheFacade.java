/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import com.enonic.cms.framework.cache.base.AbstractCacheFacade;

final class StandardCacheFacade
    extends AbstractCacheFacade
{
    private final StandardCache peer;

    public StandardCacheFacade( final StandardCache peer )
    {
        this.peer = peer;
    }

    @Override
    public Object doGet( final String compositeKey )
    {
        final CacheEntry entry = this.peer.get( compositeKey );

        if ( entry == null )
        {
            return null;
        }

        return entry.getValue();
    }

    @Override
    public void doPut( final String compositeKey, final Object value, final int timeToLive )
    {
        final CacheEntry entry = new CacheEntry( compositeKey, value, timeToLive > 0 ? timeToLive * 1000L : 0 );
        this.peer.put( entry );
    }

    @Override
    public int getCount()
    {
        return this.peer.numberOfEntries();
    }

    @Override
    public void doRemove( final String compositeKey )
    {
        this.peer.remove( compositeKey );
    }

    @Override
    public void doRemoveGroup( final String groupName )
    {
        if ( groupName != null )
        {
            this.peer.removeGroup( groupName );
        }
        else
        {
            this.peer.removeAll();
        }
    }

    @Override
    public void doRemoveGroupByPrefix( final String prefix )
    {
        if ( prefix != null )
        {
            this.peer.removeGroupByPrefix( prefix );
        }
        else
        {
            this.peer.removeAll();
        }
    }

    @Override
    public void doRemoveAll()
    {
        this.peer.removeAll();
    }
}
