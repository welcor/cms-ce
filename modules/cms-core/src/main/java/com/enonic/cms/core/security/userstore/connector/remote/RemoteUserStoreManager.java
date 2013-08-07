/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStoreFactory;
import com.enonic.cms.core.plugin.ext.ExtensionListener;

@Component
public final class RemoteUserStoreManager
    implements ExtensionListener
{
    private Runnable refreshCallback;

    private final List<RemoteUserStoreFactory> factoryList;

    public RemoteUserStoreManager()
    {
        this.factoryList = Lists.newArrayList();
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
        for ( final RemoteUserStoreFactory ext : this.factoryList )
        {
            if ( ext.isOfType( type ) )
            {
                return ext;
            }
        }

        throw new IllegalArgumentException( "No such RemoteUserStoreFactory [" + type + "] registered" );
    }

    @Override
    public void extensionAdded( final Extension ext )
    {
        if ( ext instanceof RemoteUserStoreFactory )
        {
            this.factoryList.add( (RemoteUserStoreFactory) ext );
        }
    }

    @Override
    public void extensionRemoved( final Extension ext )
    {
        if ( ext instanceof RemoteUserStoreFactory )
        {
            this.factoryList.remove( ext );
            this.refreshCallback.run();
        }
    }
}
