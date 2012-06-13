package com.enonic.cms.core.search;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.config.ConfigProperties;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/24/11
 * Time: 2:38 PM
 */
final class NodeSettingsBuilderImpl
    implements NodeSettingsBuilder
{
    public static final String NODE_PROPERTIES_PREFIX = "cms.elasticsearch.node";

    private final static String TRANSPORT_TCP_PORT = "transport.tcp.port";

    private ConfigProperties configProperties;

    private Logger LOG = Logger.getLogger( NodeSettingsBuilderImpl.class.getName() );

    public Settings createNodeSettings()
    {
        final Map<String, String> nodeProperyMap = configProperties.getMapStartingWith( NODE_PROPERTIES_PREFIX );

        final ImmutableSettings.Builder settingsBuilder = populateNodeSettings( nodeProperyMap );

        return settingsBuilder.build();
    }

    private ImmutableSettings.Builder populateNodeSettings( final Map<String, String> nodeProperyMap )
    {
        final ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();

        for ( String property : nodeProperyMap.keySet() )
        {
            final String nodePropertyName = getNodePropertyName( property );
            final String propertyValue = nodeProperyMap.get( property );

            LOG.info( "Setting elasticsearch node property: " + nodePropertyName + " = " + propertyValue );

            settingsBuilder.put( nodePropertyName, propertyValue );
        }
        return settingsBuilder;
    }

    private String getNodePropertyName( final String property )
    {
        return StringUtils.substringAfter( property, NODE_PROPERTIES_PREFIX + "." );
    }

    @Autowired
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;

    }
}
