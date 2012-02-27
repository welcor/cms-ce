package com.enonic.cms.core.plugin.context;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.ext.Extension;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import java.util.Hashtable;
import java.util.Map;

import com.google.common.collect.Maps;

public class PluginContextImplTest
{
    private BundleContext bundleContext;

    private PluginContextImpl pluginContext;

    private Map<String, Object> serviceMap;

    @Before
    public void setUp()
    {
        final Hashtable<String, String> headers = new Hashtable<String, String>();
        headers.put( "Bundle-Name", "Some Name" );

        this.bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "some.id" );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );
        Mockito.when( bundle.getVersion() ).thenReturn( new Version( "1.1.1" ) );
        Mockito.when( bundle.getBundleContext() ).thenReturn( this.bundleContext );

        this.serviceMap = Maps.newHashMap();
        this.pluginContext = new PluginContextImpl( bundle, this.serviceMap );
    }

    @Test
    public void testMetaData()
    {
        assertEquals( "some.id", this.pluginContext.getId() );
        assertEquals( "Some Name", this.pluginContext.getName() );
        assertEquals( "1.1.1", this.pluginContext.getVersion() );
    }

    @Test
    public void testRegister()
    {
        final Extension ext = new Extension()
        {
        };
        this.pluginContext.register( ext );

        Mockito.verify( this.bundleContext ).registerService( Extension.class.getName(), ext, null );
    }

    @Test
    public void testGetServices()
    {
        final Client service = Mockito.mock( Client.class );
        this.serviceMap.put( "client", service );

        final Map<String, Object> map = this.pluginContext.getServices();
        assertNotNull( map );
        assertEquals( 1, map.size() );
        assertSame( service, map.get( "client" ) );
    }

    @Test
    public void testGetConfig()
    {
        final PluginConfig config = Mockito.mock( PluginConfig.class );

        final ServiceReference ref = Mockito.mock( ServiceReference.class );
        Mockito.when( this.bundleContext.getServiceReference( PluginConfig.class.getName() ) ).thenReturn( ref );
        Mockito.when( this.bundleContext.getService( ref ) ).thenReturn( config );

        final PluginConfig returnedConfig = this.pluginContext.getConfig();
        assertNotNull( returnedConfig );
        assertSame( config, returnedConfig );

        final PluginConfig cachedConfig = this.pluginContext.getConfig();
        assertNotNull( cachedConfig );
        assertSame( config, cachedConfig );

        Mockito.verify( this.bundleContext, Mockito.times( 1 ) ).getService( ref );
    }
}
