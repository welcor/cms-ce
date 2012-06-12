package com.enonic.cms.core.search;

import java.io.IOException;

import org.elasticsearch.common.settings.ImmutableSettings;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/16/12
 * Time: 10:06 AM
 */
public class ContentIndexSettingsBuilder
    implements IndexSettingsBuilder
{

    protected static final String NUMBER_OF_SHARDS_VALUE = "5";

    protected static final String NUMBER_OF_REPLICAS_VALUE = "1";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE_KEY = "index.search.slowlog.threshold.query.trace";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG_KEY = "index.search.slowlog.threshold.query.debug";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO_KEY = "index.search.slowlog.threshold.query.info";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN_KEY = "index.search.slowlog.threshold.query.warn";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE_KEY = "index.search.slowlog.threshold.fetch.trace";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG_KEY = "index.search.slowlog.threshold.fetch.debug";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO_KEY = "index.search.slowlog.threshold.fetch.info";

    protected static final String INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN_KEY = "index.search.slowlog.threshold.fetch.warn";

    protected static final String NUMBER_OF_SHARDS_KEY = "number_of_shards";

    protected static final String NUMBER_OF_REPLICAS_KEY = "number_of_replicas";

    public ImmutableSettings.Builder buildSettings()
    {
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.loadFromSource( buildDistributionSettings() );
        settings.loadFromSource( buildStorageSettings() );
        settings.loadFromSource( buildAnalyserSettings() );

        buildSlowLogSettings( settings );

        return settings;
    }

    private void buildSlowLogSettings( ImmutableSettings.Builder settings )
    {
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE_KEY, "10ms" );
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG_KEY, "50ms" );
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO_KEY, "200ms" );
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN_KEY, "500ms" );

        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE_KEY, "20ms" );
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG_KEY, "100ms" );
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO_KEY, "400ms" );
        settings.put( INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN_KEY, "1s" );
    }

    private String buildStorageSettings()
    {
        /*try
        {
            return jsonBuilder().startObject().startObject( "store" ).field( "type", "memory" ).endObject().endObject().string();
        }
        catch ( IOException e )
        {
            throw new ContentIndexException( "Not able to create settings for index", e );
        }
          */

        return "";
    }

    private String buildDistributionSettings()
    {
        try
        {
            return jsonBuilder().startObject().field( NUMBER_OF_SHARDS_KEY, NUMBER_OF_SHARDS_VALUE ).field( NUMBER_OF_REPLICAS_KEY,
                                                                                                            NUMBER_OF_REPLICAS_VALUE ).endObject().string();
        }
        catch ( IOException e )
        {
            throw new ContentIndexException( "Not able to create settings for index", e );
        }
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
