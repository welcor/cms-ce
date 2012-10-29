package com.enonic.cms.core.portal.datasource.handler.extension;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.InvokeExtensionHandler")
public final class InvokeExtensionHandler
    extends SimpleDataSourceHandler
{
    private PluginManager pluginManager;

    public InvokeExtensionHandler()
    {
        super( "invokeExtension" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String name = param( req, "name" ).required().asString();


        return null;
    }

    @Autowired
    public void setPluginManager( final PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }
}
