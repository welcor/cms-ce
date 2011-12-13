package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/24/11
 * Time: 2:38 PM
 */
final class IndexSettingsBuilder {


    private final static String NODE_MEMORY_MIN = "es_mim_mem";
    private final static String NODE_MEMORY_MAX = "es_max_mem";
    private final static String TRANSPORT_TCP_PORT = "transport.tcp.port";
    private final static String HTTP_PORT = "http.port";
    private final static String LOG_PATH = "path.logs";
    private final static String DATA_PATH = "path.data";
    private final static String CLUSTER_NAME = "elasticsearch";
    private final static String NODE_NAME = "name";


    public final static Settings createNodeSettings(File storageDir) {

        return ImmutableSettings.settingsBuilder()
                .put(LOG_PATH, new File(storageDir, "log").getAbsolutePath())
                .put(DATA_PATH, new File(storageDir, "data").getAbsolutePath())
                .put(CLUSTER_NAME, "enonic.elasticsearch")
                .build();
    }

}
