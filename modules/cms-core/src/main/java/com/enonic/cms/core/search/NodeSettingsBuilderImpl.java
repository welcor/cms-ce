package com.enonic.cms.core.search;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/24/11
 * Time: 2:38 PM
 */
@Component
public final class NodeSettingsBuilderImpl
    extends AbstractElasticsearchSettingsBuilder
    implements NodeSettingsBuilder
{

    @Override
    public Settings buildNodeSettings()
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        final Map<String, String> nodePropertyMap = getNodePropertyMap();

        populateSettings( settings, nodePropertyMap, ELASTICSEARCH_PROPERTIES_PREFIX );

        return settings.build();
    }

    private Map<String, String> getNodePropertyMap()
    {
        return configProperties.getSubMap( new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, ELASTICSEARCH_PROPERTIES_PREFIX ) &&
                    !StringUtils.startsWith( input, INDEX_PROPERTIES_PREFIX );
            }
        } );
    }
}
