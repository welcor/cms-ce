package com.enonic.cms.itest.search;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import com.enonic.cms.core.search.NodeSettingsBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/7/12
 * Time: 1:38 PM
 */
public class NodeTestSettingsBuilder
    implements NodeSettingsBuilder
{
    private final static String NODE_MEMORY_MIN = "es_mim_mem";

    private final static String NODE_MEMORY_MAX = "es_max_mem";

    private final static String TRANSPORT_TCP_PORT = "transport.tcp.port";

    private final static String HTTP_PORT = "http.port";

    private final static String LOG_PATH = "path.logs";

    private final static String DATA_PATH = "path.data";

    private final static String CONFIG_PATH = "path.config";

    private final static String CLUSTER_NAME = "enonic-cms-es-cluster";

    private final static String NODE_NAME = "name";

    public Settings createNodeSettings()
    {
        return ImmutableSettings.settingsBuilder().put( "gateway.type", "none" )
            // .put( "threadpool.index.type", "blocking")
            // .put( "threadpool.index.min", 1)
            // .put( "threadpool.index.size", 50)
            // .put( "threadpool.index.wait_time", "60s" )
            .build();
    }
}
