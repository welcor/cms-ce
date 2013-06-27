/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import java.util.Set;

import org.apache.jackrabbit.webdav.DavSession;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

final class DavSessionImpl
    implements DavSession
{
    private final Set<String> lockTokens;

    public DavSessionImpl()
    {
        this.lockTokens = Sets.newHashSet();
    }

    @Override
    public void addReference( final Object reference )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference( final Object reference )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLockToken( final String token )
    {
        this.lockTokens.add( token );
    }

    @Override
    public String[] getLockTokens()
    {
        return Iterables.toArray( this.lockTokens, String.class );
    }

    @Override
    public void removeLockToken( final String token )
    {
        this.lockTokens.remove( token );
    }
}
