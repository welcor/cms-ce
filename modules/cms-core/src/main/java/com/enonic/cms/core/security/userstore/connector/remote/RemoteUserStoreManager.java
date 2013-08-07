/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStoreFactory;
import com.enonic.cms.core.plugin.ext.FilteredExtensionListener;
import com.enonic.cms.core.plugin.ext.RemoteUserStoreFactoryExtensions;

@Component
public final class RemoteUserStoreManager
    extends FilteredExtensionListener<RemoteUserStoreFactory>
{
    private Runnable refreshCallback;

    private RemoteUserStoreFactoryExtensions extensions;

    public RemoteUserStoreManager()
    {
        super( RemoteUserStoreFactory.class );
    }

    @Autowired
    public void setExtensions( final RemoteUserStoreFactoryExtensions extensions )
    {
        this.extensions = extensions;
    }

    public void setRefreshCallback( final Runnable refreshCallback )
    {
        this.refreshCallback = refreshCallback;
    }

    public RemoteUserStore create( final String type, final Properties props )
    {
        final RemoteUserStoreFactory factory = findFactory( type );
        return factory.create( props );
    }

    private RemoteUserStoreFactory findFactory( final String type )
    {
        final RemoteUserStoreFactory ext = this.extensions.getByType( type );
        if ( ext != null )
        {
            return ext;
        }

        throw new IllegalArgumentException( "No such RemoteUserStoreFactory [" + type + "] registered" );
    }

    @Override
    protected void addExtension( final RemoteUserStoreFactory ext )
    {
        // Do nothing
    }

    @Override
    protected void removeExtension( final RemoteUserStoreFactory ext )
    {
        this.refreshCallback.run();
    }
}

