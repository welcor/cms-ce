/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote.plugin;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.api.plugin.userstore.RemoteUserStore;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreManager;

import static org.junit.Assert.*;

public class RemoteUserStoreFactoryTest
{
    private RemoteUserStoreManager factory;

    @Before
    public void setUp()
    {
        this.factory = new RemoteUserStoreManager();
    }

    @Test
    public void testCustom()
    {
        RemoteUserStore dir = this.factory.create( NopRemoteUserStorePlugin.class.getName(), null );
        assertEquals( NopRemoteUserStorePlugin.class, dir.getClass() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegal()
    {
        this.factory.create( "dummy", null );
    }

    @Test
    public void testConfigure()
    {
        Properties props = new Properties();
        props.setProperty( "prop1", "hello" );
        props.setProperty( "prop2", "11" );
        props.setProperty( "prop3", "true" );

        NopRemoteUserStorePlugin dir = (NopRemoteUserStorePlugin) this.factory.create( NopRemoteUserStorePlugin.class.getName(), props );

        assertEquals( "hello", dir.getProp1() );
        assertEquals( 11, dir.getProp2() );
        assertEquals( true, dir.getProp3() );
    }
}