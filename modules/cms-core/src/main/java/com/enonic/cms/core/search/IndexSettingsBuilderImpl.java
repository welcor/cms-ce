package com.enonic.cms.core.search;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/16/12
 * Time: 10:06 AM
 */
@Component
public class IndexSettingsBuilderImpl
    extends AbstractElasticsearchSettingsBuilder
    implements IndexSettingBuilder
{
    public Settings buildIndexSettings()
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();

        final Map<String, String> indexPropertyMap = getIndexPropertyMap();

        populateSettings( settings, indexPropertyMap, ELASTICSEARCH_PROPERTIES_PREFIX );

        settings.loadFromSource( buildAnalyserSettings() );

        return settings.build();
    }

    private Map<String, String> getIndexPropertyMap()
    {
        return configProperties.getSubMap( new Predicate<String>()
        {
            @Override
            public boolean apply( final String input )
            {
                return StringUtils.startsWith( input, INDEX_PROPERTIES_PREFIX );
            }
        } );
    }

    private String buildAnalyserSettings()
    {
        try
        {
            return jsonBuilder().startObject().startObject( "analysis" ).startObject( "analyzer" ).startObject(
                "whitespace_analyzer" ).field( "type", "custom" ).field( "tokenizer", "whitespace" ).field( "filter", new String[]{
                "lowercase"} ).endObject().endObject().endObject().endObject().string();
        }
        catch ( IOException e )
        {
            throw new ContentIndexException( "Not able to create analyzer settings for index", e );
        }

    }


}
