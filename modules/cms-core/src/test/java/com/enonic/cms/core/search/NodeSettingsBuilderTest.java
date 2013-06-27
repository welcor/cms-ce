/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

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
        configProperties.setProperty( "cms.elasticsearch.node.client", "false" );
        configProperties.setProperty( "cms.elasticsearch.path.logs", "logpath" );
        configProperties.setProperty( "cms.elasticsearch.index.indexname", "indexname" );
        builderImpl.setConfigProperties( configProperties );

        final Settings settings = builderImpl.buildNodeSettings();

        assertEquals( "logpath", settings.get( "path.logs" ) );
        assertEquals( "false", settings.get( "node.client" ) );
        assertEquals( "true", settings.get( "node.local" ) );
        assertEquals( 3, settings.getAsMap().keySet().size() );


    }

    @Test
    public void use_cluster_enabled_for_client_as_default()
    {
        NodeSettingsBuilder builderImpl = new NodeSettingsBuilder();

        ConfigProperties configProperties = new ConfigProperties();
        configProperties.put( "cms.cluster.enabled", "true" );
        builderImpl.setConfigProperties( configProperties );

        final Settings settings = builderImpl.buildNodeSettings();

        assertEquals( "false", settings.get( "node.local" ) );
    }

}
