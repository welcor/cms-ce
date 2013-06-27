/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.tools.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.tools.AbstractToolController;

public final class PluginInfoController
    extends AbstractToolController
{
    private PluginManager pluginManager;

    @Autowired
    public void setPluginManager( final PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String updateKey = req.getParameter( "update" );

        if ( updateKey != null )
        {
            doUpdatePlugin( new Long( updateKey ), req, res );
        }

        final HashMap<String, Object> model = new HashMap<String, Object>();

        final ExtensionSet extensions = this.pluginManager.getExtensions();
        model.put( "baseUrl", getBaseUrl( req ) );
        model.put( "functionLibraryExtensions", toWrappers( extensions.getAllFunctionLibraries() ) );
        model.put( "autoLoginExtensions", toWrappers( extensions.getAllHttpAutoLoginPlugins() ) );
        model.put( "httpInterceptors", toWrappers( extensions.getAllHttpInterceptors() ) );
        model.put( "httpResponseFilters", toWrappers( extensions.getAllHttpResponseFilters() ) );
        model.put( "taskExtensions", toWrappers( extensions.getAllTaskPlugins() ) );
        model.put( "textExtractorExtensions", toWrappers( extensions.getAllTextExtractorPlugins() ) );
        model.put( "pluginHandles", toPluginWrappers( this.pluginManager.getPlugins() ) );

        renderView( req, res, model, "pluginInfoPage" );
    }

    private void doUpdatePlugin( final long pluginKey, final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final PluginHandle handle = this.pluginManager.findPluginByKey( pluginKey );
        if ( handle != null )
        {
            handle.update();
        }

        redirectToReferrer( req, res );
    }

    private Collection<ExtensionWrapper> toWrappers( final List<? extends Extension> list )
    {
        return ExtensionWrapper.toWrapperList( list );
    }

    private Collection<PluginWrapper> toPluginWrappers( final List<PluginHandle> list )
    {
        return PluginWrapper.toWrapperList( list );
    }
}
