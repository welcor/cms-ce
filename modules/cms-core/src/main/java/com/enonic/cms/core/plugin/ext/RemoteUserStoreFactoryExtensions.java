package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;

import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStoreFactory;

@Component
public final class RemoteUserStoreFactoryExtensions
    extends ExtensionPoint<RemoteUserStoreFactory>
{
    public RemoteUserStoreFactoryExtensions()
    {
        super( RemoteUserStoreFactory.class );
    }

    public RemoteUserStoreFactory getByType( final String type )
    {
        for ( final RemoteUserStoreFactory ext : this )
        {
            if ( ext.isOfType( type ) )
            {
                return ext;
            }
        }

        return null;
    }

    @Override
    protected String toHtml( final RemoteUserStoreFactory ext )
    {
        return composeHtml( ext, "type", ext.getType(), "aliases", Joiner.on( ", " ).skipNulls().join( ext.getAliases() ) );
    }

    @Override
    public int compare( final RemoteUserStoreFactory o1, final RemoteUserStoreFactory o2 )
    {
        return o1.compareTo( o2 );
    }
}
