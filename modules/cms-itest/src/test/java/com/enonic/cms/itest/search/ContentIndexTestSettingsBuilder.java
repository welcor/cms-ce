package com.enonic.cms.itest.search;

import java.io.IOException;

import org.elasticsearch.common.settings.ImmutableSettings;

import com.enonic.cms.core.search.ContentIndexException;
import com.enonic.cms.core.search.IndexSettingsBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/16/12
 * Time: 10:17 AM
 */
public class ContentIndexTestSettingsBuilder
    implements IndexSettingsBuilder
{

    protected static final String NUMBER_OF_SHARDS = "5";

    protected static final String NUMBER_OF_REPLICAS = "1";


    public ImmutableSettings.Builder buildSettings()
    {
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.loadFromSource( buildDistributionSettings() );
        settings.loadFromSource( buildStorageSettings() );
        settings.loadFromSource( buildAnalyserSettings() );

        return settings;
    }

    private String buildStorageSettings()
    {
        try
        {
            return jsonBuilder().startObject().startObject( "store" ).field( "type", "memory" ).endObject().endObject().string();
        }
        catch ( IOException e )
        {
            throw new ContentIndexException( "Not able to create settings for index", e );
        }
    }

    private String buildDistributionSettings()
    {
        try
        {
            return jsonBuilder().startObject()
                .field( "number_of_shards", NUMBER_OF_SHARDS )
                .field( "number_of_replicas", NUMBER_OF_REPLICAS )
                .endObject()
                .string();
        }
        catch ( IOException e )
        {
            throw new ContentIndexException( "Not able to create settings for index", e );
        }
    }

    private String buildAnalyserSettings()
    {
        /* return jsonBuilder()
             .startObject()
                 .startObject( "analysis" )
                     .startObject( "analyzer" )
                         .startObject( "keywordlowercase" )
                             .field( "type", "custom" )
                             .field( "tokenizer", "keyword" )
                             .field( "filter", new String[]{"lowercase"} )
                         .endObject()
                     .endObject()
                 .endObject()
             .endObject()
             .string();
        */
        return "";
    }

}


