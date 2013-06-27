/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.enonic.cms.core.plugin.context.PluginContext;

final class BeansProcessor
    implements BeanFactoryPostProcessor
{
    private final PluginContext context;

    public BeansProcessor( final PluginContext context )
    {
        this.context = context;
    }

    public void postProcessBeanFactory( final ConfigurableListableBeanFactory factory )
        throws BeansException
    {
        factory.registerSingleton( "plugin.context", this.context );
        factory.registerSingleton( "plugin.config", this.context.getConfig() );

        for ( final Map.Entry<String, Object> entry : this.context.getServices().entrySet() )
        {
            factory.registerSingleton( "plugin.service." + entry.getKey(), entry.getValue() );
        }
    }
}
