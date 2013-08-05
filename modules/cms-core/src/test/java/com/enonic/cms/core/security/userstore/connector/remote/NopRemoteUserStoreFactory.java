package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.Properties;

import org.mockito.Mockito;

import com.enonic.cms.api.plugin.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.userstore.RemoteUserStoreFactory;

public final class NopRemoteUserStoreFactory
    extends RemoteUserStoreFactory
{
    public NopRemoteUserStoreFactory()
    {
        super( "nop" );
    }

    @Override
    public RemoteUserStore create( final Properties props )
    {
        return Mockito.mock( RemoteUserStore.class );
    }
}
