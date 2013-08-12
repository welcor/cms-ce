/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.plugin.ext.userstore;

public abstract class RemotePrincipal
{
    private final String id;

    private String sync;

    public RemotePrincipal( final String id )
    {
        this.id = id;
    }

    public RemotePrincipal( final RemotePrincipal other )
    {
        this.id = other.id;
        this.sync = other.sync;
    }

    public final String getId()
    {
        return this.id;
    }

    public final String getSync()
    {
        return sync;
    }

    public final void setSync( String sync )
    {
        this.sync = sync;
    }
}
