/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ext.ExtensionListener;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.container.OsgiContainer;
import com.enonic.cms.core.plugin.context.ContextFactory;
import com.enonic.cms.core.plugin.util.OsgiHelper;

@Component("pluginManager")
public final class PluginManagerImpl
    extends OsgiContainer
{
    private final ExtensionHolder holder;

    private ContextFactory contextFactory;

    private BundleContext bundleContext;

    public PluginManagerImpl()
    {
        this.holder = new ExtensionHolder();
    }

    @Override
    protected void start( final BundleContext context )
        throws Exception
    {
        this.bundleContext = context;

        final ExtensionTracker tracker = new ExtensionTracker( this.bundleContext, this.holder );
        tracker.open();

        this.contextFactory.register( this.bundleContext );
    }

    public List<PluginHandle> getPlugins()
    {
        if ( this.bundleContext == null )
        {
            return Collections.emptyList();
        }

        final ArrayList<PluginHandle> list = Lists.newArrayList();
        for ( final Bundle bundle : getBundles() )
        {
            list.add( new PluginHandleImpl( bundle, this.holder ) );
        }

        return list;
    }

    private List<Bundle> getBundles()
    {
        final ArrayList<Bundle> list = Lists.newArrayList();
        for ( final Bundle bundle : this.bundleContext.getBundles() )
        {
            if ( !OsgiHelper.isFrameworkBundle( bundle ) )
            {
                list.add( bundle );
            }
        }

        return list;
    }

    public PluginHandle findPluginByKey( final long key )
    {
        for ( final PluginHandle plugin : getPlugins() )
        {
            if ( plugin.getKey() == key )
            {
                return plugin;
            }
        }

        return null;
    }

    @Autowired(required = false)
    public void setListeners( final List<ExtensionListener> list )
    {
        this.holder.setListeners( list );
    }

    @Autowired(required = false)
    public void setLocalExtensions( final List<Extension> list )
    {
        for ( final Extension ext : list )
        {
            this.holder.add( LocalServiceReference.INSTANCE, ext );
        }
    }

    public ExtensionSet getExtensions()
    {
        return new ExtensionSetImpl( this.holder.getAll() );
    }

    @Autowired
    public void setContextFactory( final ContextFactory contextFactory )
    {
        this.contextFactory = contextFactory;
    }
}
