package com.enonic.cms.core.plugin.config;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.core.config.ConfigProperties;

import static org.junit.Assert.*;

public class ConfigFactoryImplTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File defaultFile;

    private ConfigFactoryImpl factory;

    @Before
    public void setUp()
        throws Exception
    {
        final Properties bundleProps = new Properties();
        bundleProps.put( "key1", "value1" );
        bundleProps.put( "key2", "value2 ${default1}" );
        bundleProps.put( "key3", "${external1}" );

        final File propFile = this.folder.newFile( "my.sample.plugin.properties" );
        bundleProps.store( new FileWriter( propFile ), "bundle properties" );

        final Properties defaultProps = new Properties();
        defaultProps.put( "default1", "default-value1" );
        defaultProps.put( "default2", "default-value2" );

        this.defaultFile = this.folder.newFile( "default.properties" );
        defaultProps.store( new FileWriter( this.defaultFile ), "default properties" );

        this.factory = new ConfigFactoryImpl();
        this.factory.setConfigDir( this.folder.getRoot() );

        final ConfigProperties globalProperties = new ConfigProperties();
        globalProperties.put( "external1", "external-value1" );
        this.factory.setGlobalProperties( globalProperties );
    }

    @Test
    public void testCreate()
        throws Exception
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "my.sample.plugin" );
        Mockito.when( bundle.getEntry( "META-INF/cms/default.properties" ) ).thenReturn( this.defaultFile.toURI().toURL() );

        final PluginConfig config = this.factory.create( bundle );
        assertEquals( 5, config.size() );
        assertEquals( "value1", config.getString( "key1" ) );
        assertEquals( "value2 default-value1", config.getString( "key2" ) );
        assertEquals( "external-value1", config.getString( "key3" ) );
        assertEquals( "default-value1", config.getString( "default1" ) );
        assertEquals( "default-value2", config.getString( "default2" ) );
    }
}
