/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.core.plugin.ext.RemoteUserStoreFactoryExtensions;

import static org.junit.Assert.*;

public class RemoteUserStoreManagerTest
{
    private RemoteUserStoreManager factory;

    private RemoteUserStoreFactoryExtensions extensions;

    @Before
    public void setUp()
    {
        this.factory = new RemoteUserStoreManager();
        this.extensions = new RemoteUserStoreFactoryExtensions();
        this.factory.setExtensions( this.extensions );
    }

    @Test
    public void testCustom()
    {
        this.extensions.extensionAdded( new NopRemoteUserStoreFactory() );
        final RemoteUserStore dir = this.factory.create( "nop", null );
        assertNotNull( dir );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegal()
    {
        this.factory.create( "dummy", null );
    }
}
