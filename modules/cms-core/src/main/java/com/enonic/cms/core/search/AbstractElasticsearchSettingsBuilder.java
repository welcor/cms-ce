/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.config.ConfigProperties;

public abstract class AbstractElasticsearchSettingsBuilder
{
    final static String ELASTICSEARCH_PROPERTIES_PREFIX = "cms.elasticsearch";

    final static String INDEX_PROPERTIES_PREFIX = ELASTICSEARCH_PROPERTIES_PREFIX + ".index";

    ConfigProperties configProperties;

    private final Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchSettingsBuilder.class );

    String subtractPrefixFromProperty( final String property, final String propertyPrefix )
    {
        return StringUtils.substringAfter( property, propertyPrefix + "." );
    }

    void populateSettings( final ImmutableSettings.Builder settings, final Map<String, String> propertyMap, final String propertyPrefix )
    {
        for ( final String property : propertyMap.keySet() )
        {
            String indexProperyValue = propertyMap.get( property );
            indexProperyValue = cleanUpPropertyValue( indexProperyValue );
            final String indexPropertyName = subtractPrefixFromProperty( property, propertyPrefix );

            LOG.info( "Apply elasticsearch setting: " + indexPropertyName + " = " + indexProperyValue );

            settings.put( indexPropertyName, indexProperyValue );
        }
    }

    private String cleanUpPropertyValue( String indexProperyValue )
    {
        indexProperyValue = Strings.trimLeadingWhitespace( indexProperyValue );
        indexProperyValue = Strings.trimTrailingWhitespace( indexProperyValue );
        return indexProperyValue;
    }

    @Autowired
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;
    }

}
