/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.tools.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.plugin.ext.ExtensionPoint;
import com.enonic.cms.core.tools.AbstractToolController;

public final class PluginInfoController
    extends AbstractToolController
{
    private List<ExtensionPoint> extensionPoints;

    private PluginManager pluginManager;

    @Autowired
    public void setPluginManager( final PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }

    @Autowired
    public void setExtensionPoints( final List<ExtensionPoint> extensionPoints )
    {
        this.extensionPoints = extensionPoints;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> createExtMap()
    {
        final Map<String, List<String>> map = Maps.newTreeMap();
        for ( final ExtensionPoint point : this.extensionPoints )
        {
            map.put( point.getName(), point.toHtml() );
        }

        return map;
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

        model.put( "baseUrl", getBaseUrl( req ) );
        model.put( "extMap", createExtMap() );
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

    private Collection<PluginWrapper> toPluginWrappers( final List<PluginHandle> list )
    {
        return PluginWrapper.toWrapperList( list );
    }
}
