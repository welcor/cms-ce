package com.enonic.cms.core.search;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.config.ConfigProperties;

public abstract class AbstractElasticsearchSettingsBuilder
{
    final static String ELASTICSEARCH_PROPERTIES_PREFIX = "cms.elasticsearch";

    final static String INDEX_PROPERTIES_PREFIX = ELASTICSEARCH_PROPERTIES_PREFIX + ".index";

    ConfigProperties configProperties;

    private Logger LOG = Logger.getLogger( AbstractElasticsearchSettingsBuilder.class.getName() );

    String subtractPrefixFromProperty( final String property, final String propertyPrefix )
    {
        return StringUtils.substringAfter( property, propertyPrefix + "." );
    }

    void populateSettings( final ImmutableSettings.Builder settings, final Map<String, String> propertyMap, final String propertyPrefix )
    {
        for ( final String property : propertyMap.keySet() )
        {
            final String indexProperyValue = propertyMap.get( property );
            final String indexPropertyName = subtractPrefixFromProperty( property, propertyPrefix );

            LOG.info( "Apply elasticsearch setting: " + indexPropertyName + " = " + indexProperyValue );

            settings.put( indexPropertyName, indexProperyValue );
        }
    }

    @Autowired
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;
    }

}
