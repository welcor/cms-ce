/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.plugin.ext.userstore;

public final class RemoteGroup
    extends RemotePrincipal
{
    public RemoteGroup( final String id )
    {
        super( id );
    }

    public RemoteGroup( final RemoteGroup other )
    {
        super( other );
    }

    public int hashCode()
    {
        return this.getId().hashCode();
    }

    public boolean equals( Object o )
    {
        return ( o instanceof RemoteGroup ) && ( (RemoteGroup) o ).getId().equals( getId() );
    }
}
