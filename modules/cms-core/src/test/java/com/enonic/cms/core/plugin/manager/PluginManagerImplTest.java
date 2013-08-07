/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.manager;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.context.ContextFactory;
import com.enonic.cms.core.plugin.ext.ExtensionListener;

import static org.junit.Assert.*;

public class PluginManagerImplTest
{
    private PluginManagerImpl manager;

    private BundleContext context;

    @Before
    public void setUp()
    {
        this.manager = new PluginManagerImpl();
        this.context = Mockito.mock( BundleContext.class );

        final ContextFactory contextFactory = Mockito.mock( ContextFactory.class );
        this.manager.setContextFactory( contextFactory );
    }

    @Test
    public void testGetPlugins()
        throws Exception
    {
        List<PluginHandle> list = this.manager.getPlugins();
        assertNotNull( list );
        assertEquals( 0, list.size() );

        Mockito.when( this.context.getBundles() ).thenReturn( new Bundle[0] );
        this.manager.start( this.context );

        list = this.manager.getPlugins();
        assertNotNull( list );
        assertEquals( 0, list.size() );

        final Bundle bundle1 = Mockito.mock( Bundle.class );
        Mockito.when( bundle1.getBundleId() ).thenReturn( 0L );

        final Bundle bundle2 = Mockito.mock( Bundle.class );
        Mockito.when( bundle2.getBundleId() ).thenReturn( 1L );

        Mockito.when( this.context.getBundles() ).thenReturn( new Bundle[]{bundle1, bundle2} );
        list = this.manager.getPlugins();
        assertNotNull( list );
        assertEquals( 1, list.size() );
        assertSame( 1L, list.get( 0 ).getKey() );
    }

    @Test
    public void testSetListeners()
    {
        final List<ExtensionListener> list = Collections.emptyList();
        this.manager.setListeners( list );
    }

    @Test
    public void testFindPluginByKey()
        throws Exception
    {
        final Bundle bundle1 = Mockito.mock( Bundle.class );
        Mockito.when( bundle1.getBundleId() ).thenReturn( 0L );

        final Bundle bundle2 = Mockito.mock( Bundle.class );
        Mockito.when( bundle2.getBundleId() ).thenReturn( 1L );

        Mockito.when( this.context.getBundles() ).thenReturn( new Bundle[]{bundle1, bundle2} );
        this.manager.start( this.context );

        PluginHandle handle = this.manager.findPluginByKey( 2L );
        assertNull( handle );

        handle = this.manager.findPluginByKey( 1L );
        assertNotNull( handle );
        assertEquals( 1L, handle.getKey() );
    }
}
