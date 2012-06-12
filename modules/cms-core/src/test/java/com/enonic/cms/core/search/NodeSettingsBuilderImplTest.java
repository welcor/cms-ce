package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import com.enonic.cms.core.boot.ConfigProperties;

import static junitx.framework.Assert.assertEquals;

public class NodeSettingsBuilderImplTest
{
    @Test
    public void testCreateSettings()
        throws Exception
    {
        NodeSettingsBuilderImpl builder = new NodeSettingsBuilderImpl();

        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setProperty( "cms.elasticsearch.node.path.logs", "logpath" );
        configProperties.setProperty( "cms.elasticsearch.index.indexname", "indexname" );
        builder.setConfigProperties( configProperties );

        final Settings nodeSettings = builder.createNodeSettings();

        assertEquals( "logpath", nodeSettings.get( "path.logs" ) );
        assertEquals( 1, nodeSettings.getAsMap().keySet().size() );


    }
}
