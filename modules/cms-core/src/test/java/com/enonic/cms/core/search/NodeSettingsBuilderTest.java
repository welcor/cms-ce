package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import com.enonic.cms.core.config.ConfigProperties;

import static junitx.framework.Assert.assertEquals;

public class NodeSettingsBuilderTest
{
    @Test
    public void testCreateSettings()
        throws Exception
    {
        NodeSettingsBuilder builderImpl = new NodeSettingsBuilder();

        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setProperty( "cms.elasticsearch.node.path.logs", "logpath" );
        configProperties.setProperty( "cms.elasticsearch.index.indexname", "indexname" );
        builderImpl.setConfigProperties( configProperties );

        final Settings settings = builderImpl.buildNodeSettings();

        assertEquals( "logpath", settings.get( "path.logs" ) );
        assertEquals( 1, settings.getAsMap().keySet().size() );


    }
}
