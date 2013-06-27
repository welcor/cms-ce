/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.cms.core.plugin.config.ConfigFactory;
import com.enonic.cms.core.plugin.host.HostServices;

import static org.junit.Assert.*;

public class ContextFactoryImplTest
{
    private ContextFactoryImpl factory;

    private ConfigFactory configFactory;

    @Before
    public void setUp()
    {
        this.configFactory = Mockito.mock( ConfigFactory.class );
        final HostServices hostServices = Mockito.mock( HostServices.class );

        this.factory = new ContextFactoryImpl();
        this.factory.setConfigFactory( this.configFactory );
        this.factory.setHostServices( hostServices );
    }

    @Test
    public void testGetService()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        final Object service = this.factory.getService( bundle, null );

        assertNotNull( service );
        assertTrue( service instanceof PluginContext );

        Mockito.verify( this.configFactory, Mockito.times( 1 ) ).create( Mockito.any( Bundle.class ) );
    }

    @Test
    public void testUnGetService()
    {
        this.factory.ungetService( null, null, null );
    }

    @Test
    public void testRegister()
    {
        final BundleContext context = Mockito.mock( BundleContext.class );
        this.factory.register( context );

        Mockito.verify( context, Mockito.times( 1 ) ).registerService( PluginContext.class.getName(), this.factory, null );
    }
}
