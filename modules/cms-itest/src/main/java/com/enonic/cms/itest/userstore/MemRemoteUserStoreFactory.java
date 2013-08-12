package com.enonic.cms.itest.userstore;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStoreFactory;

@Component
public final class MemRemoteUserStoreFactory
    extends RemoteUserStoreFactory
{
    private MemUserDatabase database;

    public MemRemoteUserStoreFactory()
    {
        super( "mem" );
    }

    @Autowired
    public void setDatabase( final MemUserDatabase database )
    {
        this.database = database;
    }

    @Override
    public RemoteUserStore create( final Properties props )
    {
        return new MemRemoteUserStore( this.database );
    }
}
