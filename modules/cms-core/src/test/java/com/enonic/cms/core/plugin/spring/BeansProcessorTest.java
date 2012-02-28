package com.enonic.cms.core.plugin.spring;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.google.common.collect.Maps;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;

import static org.junit.Assert.*;

public class BeansProcessorTest
{
    private PluginContext context;

    private ConfigurableListableBeanFactory factory;
    
    private Map<String, Object> serviceMap;

    @Before
    public void setUp()
    {
        this.context = Mockito.mock( PluginContext.class );
        this.factory = new DefaultListableBeanFactory();       
        
        this.serviceMap = Maps.newHashMap();
        Mockito.when( this.context.getServices() ).thenReturn( this.serviceMap );
    }

    private void processBeanFactory()
    {
        final BeansProcessor processor = new BeansProcessor( this.context );
        processor.postProcessBeanFactory( this.factory );
    }

    @Test
    public void testPluginContext()
    {
        processBeanFactory();
        checkBean( "plugin.context", PluginContext.class, this.context );
    }

    @Test
    public void testPluginConfig()
    {
        final PluginConfig config = Mockito.mock( PluginConfig.class );
        Mockito.when( this.context.getConfig() ).thenReturn( config );

        processBeanFactory();

        checkBean( "plugin.config", PluginConfig.class, config );
    }

    @Test
    public void testClientService()
    {
        final Client client = Mockito.mock( Client.class );
        this.serviceMap.put( "client", client );

        processBeanFactory();
        checkBean("plugin.service.client", Client.class, client);
    }

    private void checkBean( final String name, final Class<?> type, final Object value )
    {
        final Object bean1 = this.factory.getBean( name );
        assertNotNull( bean1 );
        assertSame( value, bean1 );

        final Object bean2 = this.factory.getBean( type );
        assertNotNull( bean2 );
        assertSame( value, bean2 );
    }
}
