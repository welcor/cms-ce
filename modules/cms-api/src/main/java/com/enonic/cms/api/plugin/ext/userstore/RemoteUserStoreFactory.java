package com.enonic.cms.api.plugin.ext.userstore;

import java.util.Properties;

import com.enonic.cms.api.plugin.ext.ExtensionBase;

public abstract class RemoteUserStoreFactory
    extends ExtensionBase
    implements Comparable<RemoteUserStoreFactory>
{
    private final String type;

    private final String[] aliases;

    public RemoteUserStoreFactory( final String type, final String... aliases )
    {
        this.type = type;
        this.aliases = aliases;
    }

    public final String getType()
    {
        return this.type;
    }

    public final String[] getAliases()
    {
        return this.aliases;
    }

    public final boolean isOfType( final String type )
    {
        if ( this.type.equalsIgnoreCase( type ) )
        {
            return true;
        }

        for ( final String alias : this.aliases )
        {
            if ( alias.equalsIgnoreCase( type ) )
            {
                return true;
            }
        }

        return false;
    }

    public abstract RemoteUserStore create( Properties props );

    @Override
    public final int compareTo( final RemoteUserStoreFactory o )
    {
        return this.type.compareTo( o.type );
    }
}
